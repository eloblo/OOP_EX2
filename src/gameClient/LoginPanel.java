package gameClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

public class LoginPanel implements ActionListener {

    private static boolean _open = true;
    private static int _scenario = 0;
    private static int _id = -1;
    private static JComboBox _sceneNum;
    private static JButton _freePlay;
    private static JButton _loginButton;
    private static JTextField _userTxt;
    private static JFrame login;

    public static void main(String[] a){
      //  loginPanel();
    }

    public static void loginPanel(){
        JPanel panel = new JPanel();
        login = new JFrame();
        login.setSize(330,160);
        panel.setLayout(null);
       // login.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        login.add(panel);
        JLabel user = new JLabel("User:");
        user.setBounds(10,20,80,25);

        JLabel scene = new JLabel("Scenario:");
        scene.setBounds(10,50,80,25);

        _userTxt = new JTextField(20);
        _userTxt.setBounds(100,20,165,25);

        String scenes[] = new String[24];
        for(int i = 0; i < 24; i++){
            scenes[i] = String.valueOf(i);
        }
        _sceneNum = new JComboBox(scenes);
        _sceneNum.addActionListener(new LoginPanel());
        _sceneNum.setBounds(100,50,165,25);

        _freePlay = new JButton("Free Play");
        _freePlay.addActionListener(new LoginPanel());
        _freePlay.setBounds(10,80,120,30);

        _loginButton = new JButton("Login");
        _loginButton.addActionListener(new LoginPanel());
        _loginButton.setBounds(170,80,120,30);

        panel.add(user);
        panel.add(scene);
        panel.add(_userTxt);
        panel.add(_sceneNum);
        panel.add(_freePlay);
        panel.add(_loginButton);
        login.setVisible(true);
    }

    public void actionPerformed(ActionEvent e){
        if(e.getSource() == _sceneNum){
            int scenario = _sceneNum.getSelectedIndex();
            _scenario = scenario;
        }
        if(e.getSource() == _freePlay){
            _open = false;
        }
        if(e.getSource() == _loginButton){
            try{
                int id = Integer.parseInt(_userTxt.getText());
                if(id > 0){
                    _id = id;
                    _open = false;
                }
            }
            catch (Exception ex){
            }
        }
    }

    public void dispose(){
        login.dispose();
    }

    public boolean isOpen() {
        return _open;
    }

    public int getID(){
        return _id;
    }

    public int getScenario(){
        return _scenario;
    }
}
