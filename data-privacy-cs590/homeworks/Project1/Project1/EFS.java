
import java.io.File;
import java.util.Arrays;
/**
 *
 * @author 
 */
public class EFS extends Utility{
    
    int SALT_LENGTH         = 16;       
    int KEY_LENGTH          = 16;
    int HASH_LENGTH         = 32;
    int IV_LENGTH           = 16;
    int USERNAME_LENGTH     = 128;
    int TAG_LENGTH          = 32;
    int FILE_LENGTH         = 4;
    int ENCRYPTED0_LENGTH   = 784;      // 1024-16*3-32-128-32
    int PLAIN0_LENGTH       = 764;      // 1024-16*3-32-128-32-4
    int ENCRYPTEDi_LENGTH   = 992;      // 1024-32
    int PLAINi_LENGTH       = 976;      // 1024-32-16
    int ENCRYPTED0_POSITION = 208;
    int ENCRYPTEDi_POSITION = 0;
    int MAC_POSITION        = 992;
    public EFS(Editor e)
    {
        super(e);
        set_username_password();
    }
    /*Some helper functions*/
    // Concatenation two byte array
    private byte[] ByteArrayConcatenation(byte[] a, byte[] b)
    {
        byte[] temp = new byte[a.length + b.length];
        System.arraycopy(a, 0, temp, 0, a.length);
        System.arraycopy(b, 0, temp, a.length, b.length);
        return temp;
    }
    //print hexDump
    private void hexDump(byte[] bytes)
    {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (byte b : bytes) {
            sb.append(String.format("%02x ", b));
            i++;
            if(i%16 == 0)
                sb.append("\r\n");
        }
        System.out.print(sb.toString());
        System.out.println("");
    }
    //  int--> bytes
    private byte[] intToByteArray(int value) {
        return new byte[] {
            (byte)(value >> 24),
            (byte)(value >> 16),
            (byte)(value >> 8),
            (byte)value};
    }
    // byte[] --> int
    private int ByteArrayToInt(byte[] input) {
        int value=  (input[0]<<24)&0xff000000|
                    (input[1]<<16)&0x00ff0000|
                    (input[2]<< 8)&0x0000ff00|
                    (input[3]<< 0)&0x000000ff;
        return value;
    }
    // byte[] IV = IV+1
    private byte[] increment(byte[] IV) throws Exception
    {
        int len = IV.length;
        byte[] tempIV = copyRange(IV, 0, len);

        int i= (IV[len-4]<<24)&0xff000000|
               (IV[len-3]<<16)&0x00ff0000|
               (IV[len-2]<< 8)&0x0000ff00|
               (IV[len-1]<< 0)&0x000000ff;
        i++;
        tempIV[len-4] = (byte) (i >>24);
        tempIV[len-3] = (byte) (i >>16);
        tempIV[len-2] = (byte) (i >>8);
        tempIV[len-1] = (byte) (i >>0);
        return tempIV;
    }

    // a XOR b if 
    public static byte[] XOR2Array(byte[] a, byte[] b) throws Exception
    {
        byte[] result = new byte[a.length];
        int temp1,temp2,temp;
        if (a.length != b.length)
        {
            System.out.println("[-] Two array has different length");
            throw new Exception("[-] Different Length when xor");
        }
        for (int i = 0; i < result.length; i++) 
        {
            temp1 = (int) a[i];
            temp2 = (int) b[i];
            temp = temp1 ^ temp2;
            result[i] = (byte)(0xff & temp);
        }
        return result;
    }

    private byte[] copyRange(byte[] plaintext, int Start, int End) throws Exception
    {
        int len = End-Start;
        byte[] temp = new byte[len];
        for (int i = 0; i < len; i++ ) 
        {
            temp[i] = plaintext[Start+i];
            
        }
        return temp;
    }

    /*
        Key Derivation
    */
    public byte[] DeriveEncryptionKey(String password, byte[] enc_salt) throws Exception
    {
        byte[] enc_key = new byte[KEY_LENGTH];
        byte[] temp_key = hash_SHA256(ByteArrayConcatenation(password.getBytes(),enc_salt));
        for (int i = 0; i < KEY_LENGTH ; i++) 
        {
            enc_key[i] = temp_key[i];
        }
        return enc_key;
    }
    private byte[] DeriveMacKey(String password, byte[] mac_salt) throws Exception
    {
        byte[] mac_key = new byte[KEY_LENGTH];
        byte[] temp_key = hash_SHA256(ByteArrayConcatenation(password.getBytes(), mac_salt));
        for (int i = 0; i < KEY_LENGTH ; i++) 
        {
            mac_key[i] = temp_key[i];
        }
        return mac_key;
    }

    private byte[] encrypt_AES_CTR(byte[] plaintext, byte[] key) throws Exception
    {
        if (plaintext.length%16 != 0) 
        {
            System.out.println("[-] Length of plaintext is not multiple of 16");
            throw new Exception("[-] Plain is not padded");
        }
        int plainTextBlock = plaintext.length/16;
        byte[] ciphertext = new byte[plaintext.length+IV_LENGTH];
        byte[] IV = secureRandomNumber(IV_LENGTH);                         //16 bytes IV
        //copy CTR vector
        for (int i=0;i < IV_LENGTH; i++) 
        {
            ciphertext[i] = IV[i];    
        }
        // start encrypting block by bloc
        byte[] tempBlock = new byte[IV_LENGTH];
        byte[] tempPlainBlock = new byte[IV_LENGTH];
        byte[] tempCipher= new byte[IV_LENGTH];
        for (int i=0;i < plainTextBlock; i++) 
        {
            //increment IV
            IV = increment(IV); 
            //encrypt IV
            tempBlock = encript_AES(IV, key);
            tempPlainBlock = copyRange(plaintext,i*IV_LENGTH,i*IV_LENGTH+IV_LENGTH);
            //XOR with
            tempCipher = XOR2Array(tempBlock,tempPlainBlock);
            //copy to long cipher
            for (int j=0;j<IV_LENGTH ; j++) 
            {
                ciphertext[IV_LENGTH+i*IV_LENGTH+j] = tempCipher[j]; 
            }
        }
        return ciphertext;
    }
    // Crypto tools
    private byte[] decrypt_AES_CTR(byte[] ciphertext, byte[] key) throws Exception
    {
        int blockNumber = ciphertext.length/16;
        byte[] IV = copyRange(ciphertext,0,16);                         
        byte[] tempBlock = new byte[IV_LENGTH];
        byte[] tempPlain = new byte[IV_LENGTH];
        byte[] tempCipher= new byte[IV_LENGTH];
        byte[] plaintextWithPadding = new byte[ciphertext.length - IV_LENGTH];
        for (int i=0;i < blockNumber-1; i++) 
        {
            //increment IV
            IV = increment(IV);
            //decrypt IV
            tempBlock = encript_AES(IV, key);
            tempCipher = copyRange(ciphertext,i*IV_LENGTH+IV_LENGTH,i*IV_LENGTH+2*IV_LENGTH);
            //XOR with
            tempPlain = XOR2Array(tempBlock,tempCipher);
            //copy to long plain
            for (int j=0;j<IV_LENGTH ; j++) 
            {
                plaintextWithPadding[i*IV_LENGTH+j] = tempPlain[j]; 
            }
        }
        return plaintextWithPadding;
    }

    private byte[] MAC_SHA256(byte[] message, byte[] mac_key) throws Exception
    {
        byte[] ipad = new byte[KEY_LENGTH];
        byte[] opad = new byte[KEY_LENGTH];
        for (int i = 0; i < KEY_LENGTH; ++i) {
            ipad[i] = 0x36;
            opad[i] = 0x5c;
        }
        ipad = XOR2Array(ipad,mac_key);
        opad = XOR2Array(opad,mac_key);
        byte[] tempOPAD=hash_SHA256(ByteArrayConcatenation(ipad,message));
        byte[] tag = hash_SHA256(ByteArrayConcatenation(opad,tempOPAD));
        return tag;
    }
    private boolean MAC_SHA256_Verify(byte[] message, byte[] mac_key, byte[] tag) throws Exception
    {
        byte[] tempTag = MAC_SHA256(message, mac_key);
        return Arrays.equals(tempTag,tag);
    }


    private boolean verifyPasswordAndDigest(byte[] user_name, byte[] passwordDigest, byte[] passwd_salt, String password) throws Exception
    {
        byte[] temp = hash_SHA256(ByteArrayConcatenation(user_name,
                                  ByteArrayConcatenation(password.getBytes(),passwd_salt)));

        return Arrays.equals(temp, passwordDigest);
    }
    // determine starting block given starting_position
    private int startBlock(int starting_position)
    {
        int i;
        if(starting_position < PLAIN0_LENGTH)                            /*if start less than length of first block means in block 0.*/
            return 0;
        else if (starting_position < PLAINi_LENGTH+PLAIN0_LENGTH)        /*if start less than length of first block + second means in block 0.*/
            return 1;
        else
        {
            return 1+startBlock(starting_position-PLAINi_LENGTH);        /*this can be a problem*/ 
        }
    }

    // more getter function
    private byte[] get_passwd_salt(String file_name) throws Exception
    {
        File root = new File(file_name);
        File meta = new File(root, "0");
        byte[] firstBlock = read_from_file(meta);
        byte[] passwd_salt = copyRange(firstBlock,0,SALT_LENGTH);
        return passwd_salt;
    }

    private byte[] get_enc_salt(String file_name) throws Exception
    {
        File root = new File(file_name);
        File meta = new File(root, "0");
        byte[] firstBlock = read_from_file(meta);
        byte[] enc_salt = copyRange(firstBlock,SALT_LENGTH,2*SALT_LENGTH);        
        return enc_salt;
    }

    private byte[] get_mac_salt(String file_name) throws Exception
    {
        File root = new File(file_name);
        File meta = new File(root, "0");
        byte[] firstBlock = read_from_file(meta);
        byte[] mac_salt = copyRange(firstBlock,2*SALT_LENGTH,3*SALT_LENGTH);
        return mac_salt;
    }   
    private byte[] get_passwd_digest(String file_name) throws Exception
    {
        File root = new File(file_name);
        File meta = new File(root, "0");
        byte[] firstBlock = read_from_file(meta);
        byte[] passwordDigest = copyRange(firstBlock,3*SALT_LENGTH,3*SALT_LENGTH+HASH_LENGTH);
        return passwordDigest;
    }

    private byte[] get_ciphertext(String file_name, int blocknum) throws Exception
    {
        File root = new File(file_name);
        File meta = new File(root, ""+blocknum);
        byte[] firstBlock = read_from_file(meta);
        if (blocknum == 0) // return ciphertext in block num 
        {
            byte[] cipher = copyRange(firstBlock,ENCRYPTED0_POSITION,MAC_POSITION);
            return cipher;
        }
        else
        {
            byte[] cipher = copyRange(firstBlock,0,Config.BLOCK_SIZE-TAG_LENGTH);
            return cipher;
        }
    }
        
    private byte[] findUserName(String file_name) throws Exception{
        File file = new File(file_name);
        File meta = new File(file, "0");
        byte[] firstBlock = read_from_file(meta);
        byte[] user_name  = copyRange(firstBlock,3*SALT_LENGTH+HASH_LENGTH, 3*SALT_LENGTH+HASH_LENGTH + USERNAME_LENGTH);
        return user_name;
    }

    @Override
    public void create(String file_name, String user_name, String password) throws Exception {
        dir = new File(file_name);
        if(dir.exists())
        {
            byte[] passwd_salt = get_passwd_salt(file_name);
            byte[] enc_salt = get_enc_salt(file_name);
            byte[] mac_salt = get_mac_salt(file_name);
            byte[] passwordDigest = get_passwd_digest(file_name);
            //verify password
            if (!verifyPasswordAndDigest(findUserName(file_name), passwordDigest, passwd_salt, password))
            {
                throw new PasswordIncorrectException();    
            }
            cut(file_name,0,password);
        }
        else
        {
            dir.mkdirs();
        }
        File meta = new File(dir, "0");
        byte[] passwd_salt = secureRandomNumber(SALT_LENGTH);                                         //16 bytes salt
        byte[] enc_salt    = secureRandomNumber(SALT_LENGTH);                                         //16 bytes salt
        byte[] mac_salt    = secureRandomNumber(SALT_LENGTH);                                         //16 bytes salt
        byte[] enc_key = DeriveEncryptionKey(password, enc_salt);
        byte[] mac_key = DeriveMacKey(password, mac_salt);
        byte[] file_length = new byte[FILE_LENGTH];
        file_length = intToByteArray(0);
        if(user_name.length() > 128)
        {
            System.out.println("[-] Username is too long"); 
            throw new Exception();
        }
        String toWriteString = "";
        toWriteString += user_name;
        // padding
        while (toWriteString.length() < USERNAME_LENGTH) 
        {
            toWriteString += '\0';
        }
        byte[] passwordDigest = hash_SHA256(ByteArrayConcatenation(toWriteString.getBytes(), 
                                            ByteArrayConcatenation(password.getBytes(),
                                            passwd_salt))); //32 bytes digest
        byte[] payload = ByteArrayConcatenation(passwd_salt,enc_salt);
        payload = ByteArrayConcatenation(payload, mac_salt);
        payload = ByteArrayConcatenation(payload, passwordDigest);
        payload = ByteArrayConcatenation(payload, toWriteString.getBytes());
        String contentToEncrypt="";
        while (contentToEncrypt.length() < (Config.BLOCK_SIZE - USERNAME_LENGTH - SALT_LENGTH*3 - HASH_LENGTH - TAG_LENGTH-IV_LENGTH- FILE_LENGTH)) 
        {
            contentToEncrypt += '\0';
        }
        byte[] cipher = encrypt_AES_CTR(ByteArrayConcatenation(file_length,contentToEncrypt.getBytes()), enc_key);
        payload = ByteArrayConcatenation(payload, cipher);
        byte[] tag = MAC_SHA256(payload, mac_key);
        //concatenate length 
        payload = ByteArrayConcatenation(payload, tag);
        save_to_file(payload, meta);
        return;
    }

    @Override
    public String findUser(String file_name) throws Exception {
        File file = new File(file_name);
        File meta = new File(file, "0");
        byte[] firstBlock = read_from_file(meta);
        byte[] user_name  = copyRange(firstBlock,3*SALT_LENGTH+HASH_LENGTH, 3*SALT_LENGTH+HASH_LENGTH + USERNAME_LENGTH);
        String username = byteArray2String(user_name);
        return username;
    }

    @Override
    public int length(String file_name, String password) throws Exception {
        File file = new File(file_name);
        if (!file.exists()) {
            System.out.println("[-] File doesn't exists");
            throw new Exception();
        }
        File meta = new File(file, "0");
        byte[] firstBlock = read_from_file(meta);
        // 1 verify password and derive decryption key
        byte[] passwd_salt = copyRange(firstBlock,0,SALT_LENGTH);
        byte[] enc_salt = copyRange(firstBlock,SALT_LENGTH,2*SALT_LENGTH);
        byte[] mac_salt = copyRange(firstBlock,2*SALT_LENGTH,3*SALT_LENGTH);
        byte[] passwordDigest = copyRange(firstBlock,3*SALT_LENGTH,3*SALT_LENGTH+HASH_LENGTH);
        if (!verifyPasswordAndDigest(findUserName(file_name), passwordDigest, passwd_salt, password))
        {
            throw new PasswordIncorrectException();    
        }
        byte[] enc_key = DeriveEncryptionKey(password,enc_salt);
        byte[] mac_key = DeriveMacKey(password,mac_salt);
        if (!integrityOfBlock(file_name,mac_key, 0)) {
            System.out.println("[-] Integrity Error: The metadata block has been modified");
            throw new Exception();
        }
        // 2 obtain cipher text
        byte[] cipher = get_ciphertext(file_name,0);
        // 3 decrypt
        byte[] plain = decrypt_AES_CTR(cipher, enc_key);
        // 4 obtain length
        byte[] length = copyRange(plain,0,FILE_LENGTH);
        // 5 re-encrypt
        byte[] newCipher = encrypt_AES_CTR(plain,enc_key);
        // 6 write back
        for (int i = ENCRYPTED0_POSITION ; i < MAC_POSITION; i++ ) {
            firstBlock[i] = newCipher[i-ENCRYPTED0_POSITION];
        }
        // 7 recompute hmac
        byte[] payload = copyRange(firstBlock, 0, MAC_POSITION);
        byte[] tag = MAC_SHA256(payload, mac_key);
        for (int i = MAC_POSITION; i < Config.BLOCK_SIZE; i++) 
        {
            firstBlock[i] = tag[i-MAC_POSITION];
        }
        save_to_file(firstBlock, meta);
        return ByteArrayToInt(length);
    }

    @Override
    public byte[] read(String file_name, int starting_position, int len, String password) throws Exception {
        File root = new File(file_name);
        if (!root.exists()) {
            System.out.println("[-] File doesn't exists");
            throw new Exception();
        }
        //obtain metadata
        byte[] passwd_salt = get_passwd_salt(file_name);
        byte[] enc_salt = get_enc_salt(file_name);
        byte[] mac_salt = get_mac_salt(file_name);
        byte[] passwordDigest = get_passwd_digest(file_name);
        //verify password
        if (!verifyPasswordAndDigest(findUserName(file_name),passwordDigest, passwd_salt, password))
        {
            throw new PasswordIncorrectException();    
        }
        //derive encryption keys
        byte[] enc_key = DeriveEncryptionKey(password,enc_salt);
        byte[] mac_key = DeriveMacKey(password,mac_salt);
        if (!integrityOfBlock(file_name,mac_key,0)) 
        {
            System.out.println("[-] Integrity Error: The metadata block has been modified");
            throw new Exception();
        }
        int file_length = length(file_name, password);
        if (starting_position > file_length) {
            throw new Exception();
        }
        int start_block = startBlock(starting_position);
        int end_block = startBlock(starting_position + len);
        if (end_block > startBlock(file_length)) 
        {
            System.out.println("[-] end_block does not exist");
            throw new Exception();    
        }

        byte[] content = new byte[len];
        String toReturn = "";
        int rp=0;
        for (int i = start_block; i < end_block+1; i++) 
        {

            if (!integrityOfBlock(file_name,mac_key,i)) 
            {
                System.out.println("["+i+"] Integrity Error: block has been tamper");
                throw new Exception();
            }
            if (i == 0)
            {
                //determine number of byte will be written
                int numByteToRead=0;
                if(len > PLAIN0_LENGTH-starting_position)
                {
                    numByteToRead = PLAIN0_LENGTH-starting_position;
                }
                else
                {
                    numByteToRead = len;
                }
                File meta = new File(root, Integer.toString(i));
                if(!meta.exists())
                {
                    System.out.println("[-] Should exists");
                    throw new Exception("[-] File doesn't exist");
                }
                byte[] firstBlock = read_from_file(meta);
                // obtain cipher text
                byte[] cipher = get_ciphertext(file_name,0);
                // decrypt
                byte[] plain = decrypt_AES_CTR(cipher, enc_key);
                // read plain
                for (int j = starting_position; j < numByteToRead; j++) {
                    content[rp] = plain[FILE_LENGTH+starting_position]; 
                    rp++;
                    starting_position++;
                    len--;
                }
                //re-encrypt
                byte[] newCipher = encrypt_AES_CTR(plain,enc_key);
                // write back
                for (int k = ENCRYPTED0_POSITION; k < MAC_POSITION; k++ ) {
                    firstBlock[k] = newCipher[k-ENCRYPTED0_POSITION];
                }
                // recompute hmac
                byte[] payload = copyRange(firstBlock, 0, MAC_POSITION);
                byte[] tag = MAC_SHA256(payload, mac_key);
                for (int m = MAC_POSITION; m < Config.BLOCK_SIZE; m++) 
                {
                    firstBlock[m] = tag[m-MAC_POSITION];
                }
                save_to_file(firstBlock, meta);
            }
            else  
            { 
                int numByteToRead=0;
                if(len > i*PLAINi_LENGTH+PLAIN0_LENGTH-starting_position)
                {
                    numByteToRead = i*PLAINi_LENGTH+PLAIN0_LENGTH-starting_position;
                }
                else
                {
                    numByteToRead = len;
                }
                File meta = new File(root, Integer.toString(i));
                if(!meta.exists())
                {
                   System.out.println("[-] Not Exists ");
                }
                byte[] firstBlock = read_from_file(meta);
                // obtain cipher text
                byte[] cipher = get_ciphertext(file_name,i);
                // decrypt
                byte[] plain = decrypt_AES_CTR(cipher, enc_key);
                // read plain
                for (int j = 0; j < numByteToRead; j++) {                    
                    content[rp]=plain[j]; 
                    System.out.println(plain[j]);
                    rp++;
                    starting_position++;
                    len--;
                }
                System.out.println("[>] finish reading block " + i);
                //re-encrypt
                byte[] newCipher = encrypt_AES_CTR(plain,enc_key);
                // write back
                for (int k = ENCRYPTEDi_POSITION ; k < MAC_POSITION; k++ ) {
                    firstBlock[k] = newCipher[k];
                }

                // recompute hmac
                byte[] payload = copyRange(firstBlock, 0, MAC_POSITION);
                byte[] tag = MAC_SHA256(payload, mac_key);
                for (int m = MAC_POSITION; m < Config.BLOCK_SIZE; m++) 
                {
                    firstBlock[m] = tag[m-MAC_POSITION];
                }
                save_to_file(firstBlock, meta);
            }

        }
        toReturn = byteArray2String(content);
        return toReturn.getBytes("UTF-8");
    }

    

    @Override
    public void write(String file_name, int starting_position, byte[] content, String password) throws Exception {
        String str_content = byteArray2String(content);
        File root = new File(file_name);
        if (!root.exists()) {
            System.out.println("[-] File doesn't exists");
            throw new Exception();
        }
        int initalStartingPosition = starting_position;
        //obtain metadata
        byte[] passwd_salt = get_passwd_salt(file_name);
        byte[] enc_salt = get_enc_salt(file_name);
        byte[] mac_salt = get_mac_salt(file_name);
        byte[] passwordDigest = get_passwd_digest(file_name);
        //verify password
        if (!verifyPasswordAndDigest(findUserName(file_name), passwordDigest, passwd_salt, password))
        {
            throw new PasswordIncorrectException();    
        }
        //derive encryption keys
        byte[] enc_key = DeriveEncryptionKey(password,enc_salt);
        byte[] mac_key = DeriveMacKey(password,mac_salt);
        if (!integrityOfBlock(file_name,mac_key,0)) 
        {
            System.out.println("[-] Integrity Error: the metadata block has been modified");
            throw new Exception();
        }
        int file_length = length(file_name, password);
        if (starting_position > file_length) {
            throw new Exception();
        }
        // int len = str_content.length();
        int len = content.length;
        int start_block = startBlock(starting_position);
        int end_block = startBlock(starting_position + len);
        int wp=0;                                                   // write pointer
        for (int i = start_block; i < end_block+1; i++)             
        {
            if (i == 0)
            {
                //determine number of byte will be written
                int numByteToWrite=0;
                if(len > PLAIN0_LENGTH-starting_position)
                {
                    numByteToWrite = PLAIN0_LENGTH-starting_position;
                }
                else
                {
                    numByteToWrite = len;
                }
                File meta = new File(root, Integer.toString(i));
                if (!integrityOfBlock(file_name,mac_key,i)) 
                {
                    System.out.println("[-] Integrity Error: block has been tamper");
                    throw new Exception();
                }
                if(!meta.exists())
                {
                    System.out.println("[-] Should exists");
                    throw new Exception("[-] File doesn't exist");
                }
                byte[] firstBlock = read_from_file(meta);
                // obtain cipher text
                byte[] cipher = get_ciphertext(file_name,0);
                // decrypt
                byte[] plain = decrypt_AES_CTR(cipher, enc_key);
                // write to plain
                for (int j = starting_position; j < numByteToWrite; j++) {
                    plain[FILE_LENGTH+starting_position] = content[wp];
                    wp++;
                    starting_position++;
                    len--;
                }
                //re-encrypt
                byte[] newCipher = encrypt_AES_CTR(plain,enc_key);
                // write back
                for (int k = ENCRYPTED0_POSITION; k < MAC_POSITION; k++ ) {
                    firstBlock[k] = newCipher[k-ENCRYPTED0_POSITION];
                }
                // recompute hmac
                byte[] payload = copyRange(firstBlock, 0, MAC_POSITION);
                byte[] tag = MAC_SHA256(payload, mac_key);
                for (int m = MAC_POSITION; m < Config.BLOCK_SIZE; m++) 
                {
                    firstBlock[m] = tag[m-MAC_POSITION];
                }
                save_to_file(firstBlock, meta);
            }
            else  
            { 
                int numByteToWrite=0;
                if(len > (i*PLAINi_LENGTH+PLAIN0_LENGTH-starting_position))
                {
                    numByteToWrite = i*PLAINi_LENGTH+PLAIN0_LENGTH-starting_position;
                }
                else
                {
                    numByteToWrite = len;
                }
                File meta = new File(root, Integer.toString(i));
                //create new block
                if(!meta.exists())
                {
                    //create a new block of empty string and hmac
                    String toWrite = "";
                    while(toWrite.length() < PLAINi_LENGTH)
                    {
                        toWrite += '\0';
                    }
                    byte[] toWriteCipher = encrypt_AES_CTR(toWrite.getBytes(), enc_key);
                    byte[] tag = MAC_SHA256(toWriteCipher,mac_key);
                    byte[] toWriteCipherTag = ByteArrayConcatenation(toWriteCipher,tag);
                    save_to_file(toWriteCipherTag, meta);
                }
                if (!integrityOfBlock(file_name,mac_key,i)) 
                {
                    System.out.println("["+i+"]");
                    // throw new Exception();
                }
                byte[] firstBlock = read_from_file(meta);
                // obtain cipher text
                byte[] cipher = get_ciphertext(file_name,i);
                // decrypt
                byte[] plain = decrypt_AES_CTR(cipher, enc_key);
                // write to plain
                for (int j = 0; j < numByteToWrite; j++) 
                {                    
                    plain[j] = content[wp];
                    System.out.println(content[wp]);
                    wp++;
                    starting_position++;
                    len--;
                }
                System.out.println("[>] End of block " + i);
                //re-encrypt
                byte[] newCipher = encrypt_AES_CTR(plain,enc_key);
                // write back
                for (int k = ENCRYPTEDi_POSITION ; k < MAC_POSITION; k++ ) {
                    firstBlock[k] = newCipher[k];
                }
                // recompute hmac
                byte[] payload = copyRange(firstBlock, 0, MAC_POSITION);
                byte[] tag = MAC_SHA256(payload, mac_key);
                for (int m = MAC_POSITION; m < Config.BLOCK_SIZE; m++) 
                {
                    firstBlock[m] = tag[m-MAC_POSITION];
                }
                save_to_file(firstBlock, meta);
            }
        }
        // update meta data
        if (content.length + initalStartingPosition > file_length) 
        {
            int newLength = content.length + initalStartingPosition;
            byte[] newLengthbyte = intToByteArray(newLength);
            File meta = new File(root, "0");
            byte[] firstBlock = read_from_file(meta);
            // obtain cipher text
            byte[] cipher = get_ciphertext(file_name,0);
            // decrypt
            byte[] plain = decrypt_AES_CTR(cipher, enc_key);
            for (int n = 0; n < FILE_LENGTH; n++) {
                plain[n] = newLengthbyte[n]; 
            }
            //re-encrypt
            byte[] newCipher = encrypt_AES_CTR(plain,enc_key);
            // write back
            for (int k = ENCRYPTED0_POSITION ; k < MAC_POSITION; k++ ) {
                firstBlock[k] = newCipher[k-ENCRYPTED0_POSITION];
            }
            // recompute hmac
            byte[] payload = copyRange(firstBlock, 0, MAC_POSITION);
            byte[] tag = MAC_SHA256(payload, mac_key);
            for (int m = MAC_POSITION; m < Config.BLOCK_SIZE; m++) 
            {
                firstBlock[m] = tag[m-MAC_POSITION];
            }
            save_to_file(firstBlock, meta);
        }
    }

    private boolean integrityOfBlock(String file_name,byte[] mac_key, int blockNum) throws Exception
    {
        File root = new File(file_name);
        if (!root.exists()) {
            System.out.println("[-] File doesn't exist");
            throw new Exception();
        }
        File meta = new File(root, Integer.toString(blockNum)); 
        byte[] firstBlock = read_from_file(meta);
        byte[] dataToCheck = copyRange(firstBlock, 0, MAC_POSITION);
        byte[] tag = copyRange(firstBlock, MAC_POSITION, Config.BLOCK_SIZE);
        if (!MAC_SHA256_Verify(dataToCheck,mac_key,tag)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean check_integrity(String file_name, String password) throws Exception {
        File root = new File(file_name);
        if (!root.exists()) {
            System.out.println("[-] File doesn't exist");
            throw new Exception();
        }
        //obtain metadata
        byte[] passwd_salt = get_passwd_salt(file_name);
        byte[] enc_salt = get_enc_salt(file_name);
        byte[] mac_salt = get_mac_salt(file_name);
        byte[] passwordDigest = get_passwd_digest(file_name);
        //verify password
        if (!verifyPasswordAndDigest(findUserName(file_name),passwordDigest, passwd_salt, password))
        {
            throw new PasswordIncorrectException();
        }
        //derive mac key
        byte[] mac_key = DeriveMacKey(password,mac_salt); 
        if (!integrityOfBlock(file_name,mac_key,0)) {
            return false;
        }
        int file_length = length(file_name, password);
        int numBlock = startBlock(file_length);       
        for (int i = 1 ; i < numBlock+1; i++) {
            if (!integrityOfBlock(file_name,mac_key,i)) 
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void cut(String file_name, int len, String password) throws Exception {
        File root = new File(file_name);
        if (!root.exists()) {
            System.out.println("[-] File doesn't exists");
            throw new Exception();
        }
        //obtain metadata
        byte[] passwd_salt = get_passwd_salt(file_name);
        byte[] enc_salt = get_enc_salt(file_name);
        byte[] mac_salt = get_mac_salt(file_name);
        byte[] passwordDigest = get_passwd_digest(file_name);
        //verify password
        if (!verifyPasswordAndDigest(findUserName(file_name), passwordDigest, passwd_salt, password))
        {
            throw new PasswordIncorrectException();    
        }
        //derive encryption keys
        byte[] enc_key = DeriveEncryptionKey(password,enc_salt);
        byte[] mac_key = DeriveMacKey(password,mac_salt);
        if (!integrityOfBlock(file_name,mac_key, 0)) 
        {
            System.out.println("[-] Integrity Error: the metadata block has been modified");
            throw new Exception();
        }
        int file_length = length(file_name, password);
        if (len > file_length) {
            System.out.println("[-] should not bigger");
            throw new Exception();
        }
        String flushString="";
        while(flushString.length() < PLAINi_LENGTH)
        {
            flushString += '\0';
        }
        write(file_name,len,flushString.getBytes(), password);
        file_length = length(file_name, password);
        int end_block = startBlock(len);
        int exist_block = startBlock(file_length);
        for (int cur = end_block+1; cur < exist_block+1; cur++) {
            File file = new File(root, Integer.toString(cur));
            while (file.exists()) {
                file.delete();
                // System.out.println("[+] Delete redundant file "+ cur);
            }
         } 
        //update meta data
        byte[] newLengthbyte = intToByteArray(len);
        File meta = new File(root, "0");
        byte[] firstBlock = read_from_file(meta);
        // obtain cipher text
        byte[] cipher = get_ciphertext(file_name,0);
        // decrypt
        byte[] plain = decrypt_AES_CTR(cipher, enc_key);
        for (int n = 0; n < FILE_LENGTH; n++) {
            plain[n] = newLengthbyte[n]; 
        }
        //re-encrypt
        byte[] newCipher = encrypt_AES_CTR(plain,enc_key);
        // write back
        for (int k = ENCRYPTED0_POSITION ; k < MAC_POSITION; k++ ) {
            firstBlock[k] = newCipher[k-ENCRYPTED0_POSITION];
        }
        // recompute hmac
        byte[] payload = copyRange(firstBlock, 0, MAC_POSITION);
        byte[] tag = MAC_SHA256(payload, mac_key);
        for (int m = MAC_POSITION; m < Config.BLOCK_SIZE; m++) 
        {
            firstBlock[m] = tag[m-MAC_POSITION];
        }
        save_to_file(firstBlock, meta);
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }  
}
