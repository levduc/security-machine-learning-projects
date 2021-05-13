
import java.io.File;
import java.util.HashMap;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTextField;
import javax.swing.undo.UndoManager;

/**
 *
 * @author Weining Yang
 */
public class Editor extends javax.swing.JFrame {

    Utility utility;
    UndoManager undo;
    HashMap<Integer, JRadioButtonMenuItem> mode_menu_list;

    /**
     * Creates new form Editor
     */
    public Editor() {
        initComponents();

        //Config.MODE = Config.MODE_SAMPLE;

        initMode();

        setMode();

        //set_pass();

        undo = new UndoManager();
        text.getDocument().addUndoableEditListener(undo);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        text = new javax.swing.JEditorPane();
        menu = new javax.swing.JMenuBar();
        menu_file = new javax.swing.JMenu();
        menu_new = new javax.swing.JMenuItem();
        menu_open = new javax.swing.JMenuItem();
        menu_save = new javax.swing.JMenuItem();
        menu_save_as = new javax.swing.JMenuItem();
        menu_edit = new javax.swing.JMenu();
        menu_copy = new javax.swing.JMenuItem();
        menu_cut = new javax.swing.JMenuItem();
        menu_paste = new javax.swing.JMenuItem();
        seperator = new javax.swing.JPopupMenu.Separator();
        menu_undo = new javax.swing.JMenuItem();
        menu_redo = new javax.swing.JMenuItem();
        menu_efs = new javax.swing.JMenu();
        menu_setpass = new javax.swing.JMenuItem();
        menu_read_pos = new javax.swing.JMenuItem();
        menu_save_pos = new javax.swing.JMenuItem();
        menu_cut_file = new javax.swing.JMenuItem();
        menu_integrity_check = new javax.swing.JMenuItem();
        menu_find_user = new javax.swing.JMenuItem();
        menu_get_file_length = new javax.swing.JMenuItem();
        menu_mode = new javax.swing.JMenu();
        menu_mode_sample = new javax.swing.JRadioButtonMenuItem();
        menu_mode_efs = new javax.swing.JRadioButtonMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setSize(new java.awt.Dimension(800, 600));
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jScrollPane1.setViewportView(text);

        getContentPane().add(jScrollPane1);

        menu_file.setText("File");

        menu_new.setText("New");
        menu_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_newActionPerformed(evt);
            }
        });
        menu_file.add(menu_new);

        menu_open.setText("Open");
        menu_open.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_openActionPerformed(evt);
            }
        });
        menu_file.add(menu_open);

        menu_save.setText("Save");
        menu_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_saveActionPerformed(evt);
            }
        });
        menu_file.add(menu_save);

        menu_save_as.setText("Save as");
        menu_save_as.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_save_asActionPerformed(evt);
            }
        });
        menu_file.add(menu_save_as);

        menu.add(menu_file);

        menu_edit.setText("Edit");

        menu_copy.setText("Copy");
        menu_copy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_copyActionPerformed(evt);
            }
        });
        menu_edit.add(menu_copy);

        menu_cut.setText("Cut");
        menu_cut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_cutActionPerformed(evt);
            }
        });
        menu_edit.add(menu_cut);

        menu_paste.setText("Paste");
        menu_paste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_pasteActionPerformed(evt);
            }
        });
        menu_edit.add(menu_paste);
        menu_edit.add(seperator);

        menu_undo.setText("Undo");
        menu_undo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_undoActionPerformed(evt);
            }
        });
        menu_edit.add(menu_undo);

        menu_redo.setText("Redo");
        menu_redo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_redoActionPerformed(evt);
            }
        });
        menu_edit.add(menu_redo);

        menu.add(menu_edit);

        menu_efs.setText("EFS");

        menu_setpass.setText("Login/Reset");
        menu_setpass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_setpassActionPerformed(evt);
            }
        });
        menu_efs.add(menu_setpass);

        menu_read_pos.setText("Read from Position");
        menu_read_pos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_read_posActionPerformed(evt);
            }
        });
        menu_efs.add(menu_read_pos);

        menu_save_pos.setText("Save to Position");
        menu_save_pos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_save_posActionPerformed(evt);
            }
        });
        menu_efs.add(menu_save_pos);

        menu_cut_file.setText("Cut File");
        menu_cut_file.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_cut_fileActionPerformed(evt);
            }
        });
        menu_efs.add(menu_cut_file);

        menu_integrity_check.setText("Check Integrity");
        menu_integrity_check.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_integrity_checkActionPerformed(evt);
            }
        });
        menu_efs.add(menu_integrity_check);

        menu_find_user.setText("Find User");
        menu_find_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_find_userActionPerformed(evt);
            }
        });
        menu_efs.add(menu_find_user);

        menu_get_file_length.setText("Get File Length");
        menu_get_file_length.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_get_file_lengthActionPerformed(evt);
            }
        });
        menu_efs.add(menu_get_file_length);

        menu.add(menu_efs);

        menu_mode.setText("Mode");

        menu_mode_sample.setSelected(true);
        menu_mode_sample.setText("Sample");
        menu_mode_sample.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_mode_sampleActionPerformed(evt);
            }
        });
        menu_mode.add(menu_mode_sample);

        menu_mode_efs.setText("EFS");
        menu_mode_efs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_mode_efsActionPerformed(evt);
            }
        });
        menu_mode.add(menu_mode_efs);

        menu.add(menu_mode);

        setJMenuBar(menu);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menu_openActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_openActionPerformed
        try {
            // TODO add your handling code here:
            File file = utility.set_dir();
            if (file == null) {
                return;
            }
            utility.dir = file;
            int len = utility.length(file.getAbsolutePath(), utility.password);

            byte[] ba = utility.read(file.getAbsolutePath(), 0, len, utility.password);
            
            for (int i = 0; i < ba.length; i++)
                System.out.println(ba[i]);

            String s = Utility.byteArray2String(ba);

            if (s != null) {
                text.setText(s);
            }
        } catch (Exception ex) {
            if (ex instanceof PasswordIncorrectException) {
                javax.swing.JOptionPane.showMessageDialog(null, "Incorrect password");
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "Cannot open file");
            }
        }
    }//GEN-LAST:event_menu_openActionPerformed

    private void menu_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_saveActionPerformed
        // TODO add your handling code here:
        try {
            if (utility.dir == null) {


                File file = utility.set_dir();
                if (file == null) {
                    return;
                }

                utility.create(file.getAbsolutePath(), utility.username, utility.password);
                utility.dir = file;

            }

            utility.write(utility.dir.getAbsolutePath(), 0, text.getText().getBytes("UTF-8"), utility.password);
            utility.cut(utility.dir.getAbsolutePath(), text.getText().length(), utility.password);
        } catch (Exception ex) {
            if (ex instanceof PasswordIncorrectException) {
                javax.swing.JOptionPane.showMessageDialog(null, "Incorrect password");
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "Cannot write to file");
            }
        }

    }//GEN-LAST:event_menu_saveActionPerformed

    private void menu_save_asActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_save_asActionPerformed
        // TODO add your handling code here:
        try {
            utility.dir = null;
            File file = utility.set_dir();
            if (file == null) {
                return;
            }
            utility.create(file.getAbsolutePath(), utility.username, utility.password);
            utility.dir = file;

            utility.write(utility.dir.getAbsolutePath(), 0, text.getText().getBytes("UTF-8"), utility.password);
            utility.cut(utility.dir.getAbsolutePath(), text.getText().length(), utility.password);
        } catch (Exception ex) {
            if (ex instanceof PasswordIncorrectException) {
                javax.swing.JOptionPane.showMessageDialog(null, "Incorrect password");
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "Cannot write to file");
            }
        }
    }//GEN-LAST:event_menu_save_asActionPerformed

    private void menu_copyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_copyActionPerformed
        // TODO add your handling code here:
        text.copy();

    }//GEN-LAST:event_menu_copyActionPerformed

    private void menu_cutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_cutActionPerformed
        // TODO add your handling code here:
        text.cut();
    }//GEN-LAST:event_menu_cutActionPerformed

    private void menu_pasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_pasteActionPerformed
        // TODO add your handling code here:
        text.paste();
    }//GEN-LAST:event_menu_pasteActionPerformed

    private void menu_undoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_undoActionPerformed
        // TODO add your handling code here:
        if (undo.canUndo()) {
            undo.undo();
        }
    }//GEN-LAST:event_menu_undoActionPerformed

    private void menu_redoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_redoActionPerformed
        // TODO add your handling code here:
        if (undo.canRedo()) {
            undo.redo();
        }
    }//GEN-LAST:event_menu_redoActionPerformed

    private void menu_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_newActionPerformed
        // TODO add your handling code here:
                // TODO add your handling code here:
        try {
            utility.dir = null;


            File file = utility.set_dir();
            if (file == null) {
                return;
            }
            utility.create(file.getAbsolutePath(), utility.username, utility.password);
            utility.dir = file;

            utility.write(utility.dir.getAbsolutePath(), 0, text.getText().getBytes("UTF-8"), utility.password);
            utility.cut(utility.dir.getAbsolutePath(), text.getText().length(), utility.password);
        } catch (Exception ex) {
            if (ex instanceof PasswordIncorrectException) {
                javax.swing.JOptionPane.showMessageDialog(null, "Incorrect password");
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "Cannot write to file");
            }
        }

    }//GEN-LAST:event_menu_newActionPerformed

    private void menu_mode_efsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_mode_efsActionPerformed
        // TODO add your handling code here:
        Config.MODE = Config.MODE_EFS;
        setMode();
    }//GEN-LAST:event_menu_mode_efsActionPerformed

    private void menu_mode_sampleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_mode_sampleActionPerformed
        // TODO add your handling code here:
        Config.MODE = Config.MODE_SAMPLE;
        setMode();
    }//GEN-LAST:event_menu_mode_sampleActionPerformed

    private void menu_setpassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_setpassActionPerformed
        // TODO add your handling code here:
        utility.set_username_password();
    }//GEN-LAST:event_menu_setpassActionPerformed

    private void menu_save_posActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_save_posActionPerformed
        // TODO add your handling code here:
        try {

            JPanel loginPanel = new JPanel();
            loginPanel.setLayout(new java.awt.GridLayout(1, 2));

            JLabel start_tag = new JLabel();
            JTextField start_field = new JTextField();

            start_tag.setText("start at position");
            loginPanel.add(start_tag);
            loginPanel.add(start_field);



            int okCxl = JOptionPane.showConfirmDialog(null, loginPanel, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            int start = 0, len = 0;

            if (okCxl == JOptionPane.OK_OPTION) {
                //len = Integer.parseInt(new String(len_field.getText()));
                start = Integer.parseInt(new String(start_field.getText()));
                if (utility.dir == null) {


                    File file = utility.set_dir();
                    if (file == null) {
                        return;
                    }

                    //utility.create(file.getAbsolutePath(), utility.username, utility.password);
                    utility.dir = file;

                }

                utility.write(utility.dir.getAbsolutePath(), start, text.getText().getBytes("UTF-8"), utility.password);

            }



        } catch (Exception ex) {
            if (ex instanceof PasswordIncorrectException) {
                javax.swing.JOptionPane.showMessageDialog(null, "Incorrect password");
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "Cannot write to file");
            }
        }
    }//GEN-LAST:event_menu_save_posActionPerformed

    private void menu_read_posActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_read_posActionPerformed
        // TODO add your handling code here:

        try {
            JPanel loginPanel = new JPanel();
            loginPanel.setLayout(new java.awt.GridLayout(2, 2));

            JLabel start_tag = new JLabel();
            JTextField start_field = new JTextField();

            start_tag.setText("start at position");
            loginPanel.add(start_tag);
            loginPanel.add(start_field);

            JLabel len_tag = new JLabel();
            JTextField len_field = new JTextField();

            len_tag.setText("length");
            loginPanel.add(len_tag);
            loginPanel.add(len_field);

            int okCxl = JOptionPane.showConfirmDialog(null, loginPanel, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            int start = 0, len = 0;

            if (okCxl == JOptionPane.OK_OPTION) {
                len = Integer.parseInt(new String(len_field.getText()));
                start = Integer.parseInt(new String(start_field.getText()));
                File file = utility.set_dir();
                if (file == null) {
                    return;
                }
                utility.dir = file;
                //int len = utility.length(file.getAbsolutePath(), utility.password);

                byte[] ba = utility.read(file.getAbsolutePath(), start, len, utility.password);

                String s = Utility.byteArray2String(ba);

                if (s != null) {
                    text.setText(s);
                }
            }



            // TODO add your handling code here:

        } catch (Exception ex) {
            if (ex instanceof PasswordIncorrectException) {
                javax.swing.JOptionPane.showMessageDialog(null, "Incorrect password");
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "Cannot read the file");
            }
        }
    }//GEN-LAST:event_menu_read_posActionPerformed

    private void menu_cut_fileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_cut_fileActionPerformed
        // TODO add your handling code here:
        try {
            JPanel loginPanel = new JPanel();
            loginPanel.setLayout(new java.awt.GridLayout(1, 2));

            JLabel start_tag = new JLabel();
            JTextField start_field = new JTextField();

            start_tag.setText("remaining length");
            loginPanel.add(start_tag);
            loginPanel.add(start_field);



            int okCxl = JOptionPane.showConfirmDialog(null, loginPanel, "Enter Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            int start = 0, len = 0;

            if (okCxl == JOptionPane.OK_OPTION) {
                //len = Integer.parseInt(new String(len_field.getText()));
                len = Integer.parseInt(new String(start_field.getText()));
                if (utility.dir == null) {


                    File file = utility.set_dir();
                    if (file == null) {
                        return;
                    }

                    //utility.create(file.getAbsolutePath(), utility.username, utility.password);
                    utility.dir = file;

                }

                utility.cut(utility.dir.getAbsolutePath(), len, utility.password);
            }






        } catch (Exception ex) {
            if (ex instanceof PasswordIncorrectException) {
                javax.swing.JOptionPane.showMessageDialog(null, "Incorrect password");
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "Cannot cut the file");
            }
        }
    }//GEN-LAST:event_menu_cut_fileActionPerformed

    private void menu_integrity_checkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_integrity_checkActionPerformed
        // TODO add your handling code here:
        try {


            File file = utility.set_dir();
            if (file == null) {
                return;
            }

            boolean b = utility.check_integrity(file.getAbsolutePath(), utility.password);

            if (b) {
                javax.swing.JOptionPane.showMessageDialog(null, "The file has NOT been modified");
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "The file has been modified");
            }

        } catch (Exception ex) {
            if (ex instanceof PasswordIncorrectException) {
                javax.swing.JOptionPane.showMessageDialog(null, "Incorrect password");
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "Cannot read the file");
            }
        }
    }//GEN-LAST:event_menu_integrity_checkActionPerformed

    private void menu_find_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_find_userActionPerformed
        try {
            // TODO add your handling code here:
            File file = utility.set_dir();
            if (file == null) {
                return;
            }
            String user = utility.findUser(file.getAbsolutePath());

            javax.swing.JOptionPane.showMessageDialog(null, "The user created the file is " + user);
        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(null, "Cannot read the file");
        }

    }//GEN-LAST:event_menu_find_userActionPerformed

    private void menu_get_file_lengthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_get_file_lengthActionPerformed
        // TODO add your handling code here:
        try {
            // TODO add your handling code here:
            File file = utility.set_dir();
            if (file == null) {
                return;
            }
            int len = utility.length(file.getAbsolutePath(), utility.password);

            javax.swing.JOptionPane.showMessageDialog(null, "The length of the file is " + len);
        } catch (Exception ex) {
            if (ex instanceof PasswordIncorrectException) {
                javax.swing.JOptionPane.showMessageDialog(null, "Incorrect password");
            } else {
                javax.swing.JOptionPane.showMessageDialog(null, "Cannot read the file");
            }
        }
    }//GEN-LAST:event_menu_get_file_lengthActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Editor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Editor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Editor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Editor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Editor().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JMenuBar menu;
    private javax.swing.JMenuItem menu_copy;
    private javax.swing.JMenuItem menu_cut;
    private javax.swing.JMenuItem menu_cut_file;
    private javax.swing.JMenu menu_edit;
    private javax.swing.JMenu menu_efs;
    private javax.swing.JMenu menu_file;
    private javax.swing.JMenuItem menu_find_user;
    private javax.swing.JMenuItem menu_get_file_length;
    private javax.swing.JMenuItem menu_integrity_check;
    private javax.swing.JMenu menu_mode;
    private javax.swing.JRadioButtonMenuItem menu_mode_efs;
    private javax.swing.JRadioButtonMenuItem menu_mode_sample;
    private javax.swing.JMenuItem menu_new;
    private javax.swing.JMenuItem menu_open;
    private javax.swing.JMenuItem menu_paste;
    private javax.swing.JMenuItem menu_read_pos;
    private javax.swing.JMenuItem menu_redo;
    private javax.swing.JMenuItem menu_save;
    private javax.swing.JMenuItem menu_save_as;
    private javax.swing.JMenuItem menu_save_pos;
    private javax.swing.JMenuItem menu_setpass;
    private javax.swing.JMenuItem menu_undo;
    private javax.swing.JPopupMenu.Separator seperator;
    private javax.swing.JEditorPane text;
    // End of variables declaration//GEN-END:variables

    private void setMode() {
        for (int key : mode_menu_list.keySet()) {
            if (key == Config.MODE) {
                mode_menu_list.get(key).setSelected(true);

            } else {
                mode_menu_list.get(key).setSelected(false);
            }
        }

        switch (Config.MODE) {
            case Config.MODE_SAMPLE:
                utility = new Sample(this);
                text.setText("");
                break;
            case Config.MODE_EFS:
                utility = new EFS(this);
                text.setText("");
                break;
        }
    }

    private void initMode() {
        mode_menu_list = new HashMap<Integer, JRadioButtonMenuItem>();
        mode_menu_list.put(Config.MODE_SAMPLE, menu_mode_sample);
        mode_menu_list.put(Config.MODE_EFS, menu_mode_efs);
    }
}
