import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;

public class Gui extends JFrame {
    private JTextField pathName;
    private JButton button;
    private JPanel rootPanel;
    private JScrollPane scroll;
    private JTree tree1;
    private JLabel label;
    private JLabel labelState;

    public Gui() {

        label.setText("Enter the folder Path");
        labelState.setForeground(Color.DARK_GRAY);
        setContentPane(rootPanel);
        setVisible(true);
        setSize(700, 500);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
        this.setLocation(x, y);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        tree1.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("C:\\"), false));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {

                    labelState.setText("Processing...");
                    doMain(pathName.getText());

                    labelState.setText("Found " + setFiles.size() + " files");

                } catch (Exception e1) {
                    labelState.setText("Error: " + "Enter correct Path");
                    JOptionPane.showMessageDialog(new JFrame(),
                            "Error: " + "Enter correct Path");


                }
            }
        });

        tree1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Desktop desktop = Desktop.getDesktop();
                File file = new File("M:\\Test");
                int selRow = tree1.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = tree1.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1) {
                    if (e.getClickCount() == 1) {

                    } else if (e.getClickCount() == 2) {
                        String pathFile = selPath.getLastPathComponent().toString();
                        String pathParentFolder = pathFile.substring(0, pathFile.lastIndexOf('\\'));
                        //pathName.setText(pathParentFolder);
                        file = new File(pathParentFolder);
                        try {
                            if (!pathFile.equals(pathName.getText()))
                                desktop.open(file);
                        } catch (IOException ignore) {
                        }
                    }
                }


            }
        });
    }


    ArrayList<File> list = new ArrayList<>();  //используется в рекурсии processFilesFromFolder(File folder)

    void doMain(String pathFolder) {
        list = new ArrayList<>();
        ArrayList<File> fileArrayList = new ArrayList<>();
        HashSet<ArrayList<File>> setFiles;

        fileArrayList = processFilesFromFolder(new File(pathFolder));

        setFiles = searchRepeatFilesArrays(fileArrayList);

        DefaultTreeModel treeModel = treeModel(pathFolder, setFiles);

        tree1.setModel(treeModel);

    }


    public ArrayList<File> processFilesFromFolder(File folder) {

        File[] folderEntries = folder.listFiles();
        for (File entry : folderEntries) {
            if (entry.isDirectory()) {
                processFilesFromFolder(entry);
                continue;
            }
            list.add(entry);
        }
        return list;
    }

    HashSet<ArrayList<File>> setFiles = new HashSet<>();

    public HashSet<ArrayList<File>> searchRepeatFilesArrays(ArrayList<File> list) {

        ArrayList<ArrayList<File>> arrList = new ArrayList<>();
        ArrayList<File> tempList;
        setFiles = new HashSet<>();


        for (int i = 0; i < list.size(); i++) {
            tempList = new ArrayList<>();
            tempList.add(list.get(i));
            for (int j = 0; j < list.size(); j++) {
                if (i != j)
                    if ((list.get(i).getName()).equals((list.get(j).getName()))) {
                        tempList.add(list.get(j));
                    }
            }
            if (tempList.size() > 1) { //если в массиве больше 1 элемента, то подходит
                tempList.sort(new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return o1.compareTo(o2);
                    }
                });
                setFiles.add(tempList);
            }

        }

        return setFiles;

    }


    public DefaultTreeModel treeModel(String rootName, HashSet<ArrayList<File>> setFiles) {


        // Создание древовидной структуры
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootName);

        // Ветви первого уровня
        if (setFiles != null) {
            for (ArrayList<File> arrFiles : setFiles) {
                DefaultMutableTreeNode currentNode = new DefaultMutableTreeNode(arrFiles.get(0).getName());
                for (File file : arrFiles) {
                    // Добавление листьев
                    currentNode.add(new DefaultMutableTreeNode(file.getAbsoluteFile(), false));
                }
                root.add(currentNode);
            }
        }

        return new DefaultTreeModel(root, true);
    }


}
