package kantaiWisp;

import javax.swing.*;
import static wisp.Nexus.print;

public class KanColleWisp {
    private JPanel panel1;
    private JButton 演习Button;
    private JButton 工厂Button;
    private JCheckBox 自动收取远程CheckBox;
    private JRadioButton 自动运行RadioButton;
    private JCheckBox 自动工厂和演习CheckBox;
    private JButton 刷新wisp文件Button;
    private JButton a11刷闪Button;
    private JButton 测试功能Button;

    private KanColleWisp() {
        演习Button.addActionListener(e -> {
            print("演习按钮被按下");
        });
        刷新wisp文件Button.addActionListener(e -> {
            print("刷新wisp文件");
            KantaiWispCore.nexus.generateReloadWisp();
        });
        测试功能Button.addActionListener(e -> {
            print("测试功能");
        });
        a11刷闪Button.addActionListener(e -> {
            print("当前舰队1-1刷闪");
        });
        工厂Button.addActionListener(e -> {
            print("启动工厂任务");
        });
        自动收取远程CheckBox.addActionListener(e -> {
            print("调整自动收取远征策略");
        });
        自动工厂和演习CheckBox.addActionListener(e -> {
            print("调整自动工厂和演习策略");
        });
        自动运行RadioButton.addActionListener(e -> {
            print("调整自动运行策略");
        });
    }

    static void initUI() {
        JFrame frame = new JFrame("KanColleWisp");
        frame.setContentPane(new KanColleWisp().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
