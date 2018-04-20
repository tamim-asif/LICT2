import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DataUpdater {
    JTextField trainer, uni, startTime, endTime, status, date, day;
    JFrame f;
    ArrayList<String> batches = new ArrayList<>();
    Firestore db;
    String id;
    String batch_name;
    ArrayList<BatchData> batch_data = new ArrayList<>();
    String[][] data_array;

    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    DataUpdater window = new DataUpdater();
                    window.f.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    DataUpdater() throws IOException, ExecutionException, InterruptedException {

        InputStream serviceAccount = new FileInputStream("lict-6c37e-firebase-adminsdk-y0zlq-bbc53283e5(1).json");
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .build();
        FirebaseApp.initializeApp(options);

        db = FirestoreClient.getFirestore();

        ApiFuture<QuerySnapshot> future = db.collection("batch_status").get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
//            System.out.println(document.getId() + " => " + document.get("university_name"));
            batches.add((String) document.get("batch_code"));
        }

        f=new JFrame("Batch Updater App");
        f.setBounds(100, 100, 730, 489);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().setLayout(null);

        setSwingComponent();

    }

    private void setSwingComponent() {
        JLabel lblBatch = new JLabel("Batch Code:");
        lblBatch.setBounds(65, 30, 80, 14);
        f.getContentPane().add(lblBatch);

        JLabel lblTrainer = new JLabel("Trainer:");
        lblTrainer.setBounds(65, 60, 80, 14);
        f.getContentPane().add(lblTrainer);

        JLabel lblEmailId = new JLabel("University:");
        lblEmailId.setBounds(65, 90, 80, 14);
        f.getContentPane().add(lblEmailId);

        JLabel lblDate = new JLabel("Date:");
        lblDate.setBounds(65, 120, 80, 14);
        f.getContentPane().add(lblDate);

        JLabel lblDay = new JLabel("Day:");
        lblDay.setBounds(65, 150, 80, 14);
        f.getContentPane().add(lblDay);

        JLabel lblStatus = new JLabel("Status:");
        lblStatus.setBounds(65, 180, 80, 14);
        f.getContentPane().add(lblStatus);

        JLabel lblStart = new JLabel("Start Time:");
        lblStart.setBounds(65, 210, 80, 14);
        f.getContentPane().add(lblStart);

        JLabel lblEnd = new JLabel("End Time:");
        lblEnd.setBounds(65, 240, 80, 14);
        f.getContentPane().add(lblEnd);

        trainer = new JTextField();
        trainer.setBounds(170, 60, 120, 20);
        f.getContentPane().add(trainer);
        trainer.setColumns(10);

        uni = new JTextField();
        uni.setBounds(170, 90, 120, 20);
        f.getContentPane().add(uni);
        uni.setColumns(10);

        date = new JTextField();
        date.setBounds(170, 120, 120, 20);
        f.getContentPane().add(date);
        date.setColumns(10);

        day = new JTextField();
        day.setBounds(170, 150, 120, 20);
        f.getContentPane().add(day);
        day.setColumns(10);

        status = new JTextField();
        status.setBounds(170, 180, 120, 20);
        f.getContentPane().add(status);
        status.setColumns(10);

        startTime = new JTextField();
        startTime.setBounds(170, 210, 120, 20);
        f.getContentPane().add(startTime);
        startTime.setColumns(10);

        endTime = new JTextField();
        endTime.setBounds(170, 240, 120, 20);
        f.getContentPane().add(endTime);
        endTime.setColumns(10);




        JComboBox cb = new JComboBox(batches.toArray());
        cb.setBounds(170, 30, 120, 20);
        f.add(cb);
//        f.setVisible(true);

        cb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ApiFuture<QuerySnapshot> future =
                        db.collection("batch_status").whereEqualTo("batch_code", (String)(cb.getSelectedItem())).get();
                // future.get() blocks on response
                List<QueryDocumentSnapshot> documents = null;
                try {
                    documents = future.get().getDocuments();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    e1.printStackTrace();
                }
                for (DocumentSnapshot document : documents) {
                    System.out.println(document.getId() + " => " + document.get("university_name"));
                    id = document.getId();
                    batch_name = (String)(cb.getSelectedItem());
                    uni.setText((String) document.get("university_name"));
                    trainer.setText((String) document.get("trainer_name"));
                    startTime.setText((String) document.get("start"));
                    endTime.setText((String) document.get("end"));
                    status.setText((String) document.get("status"));
                    date.setText((String) document.get("date"));
                    day.setText((String) document.get("day"));
                }
            }
        });

        JButton btnSubmit = new JButton("submit");

        btnSubmit.setBackground(Color.magenta);
        btnSubmit.setForeground(Color.BLUE);
        btnSubmit.setBounds(65, 280, 89, 23);
        f.getContentPane().add(btnSubmit);

        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(cb.getSelectedIndex() > -1){
                    try{
                        DocumentReference docRef = db.collection("batch_status").document(id);
                        // Add document data  with id "alovelace" using a hashmap
                        Map<String, Object> datas = new HashMap<>();
                        datas.put("batch_code", batch_name);
                        datas.put("trainer_name", trainer.getText());
                        datas.put("university_name", uni.getText());
                        datas.put("date", date.getText());
                        datas.put("day", day.getText());
                        datas.put("start", startTime.getText());
                        datas.put("end", endTime.getText());
                        datas.put("status", status.getText());
                        datas.put("attendance", "");
                        datas.put("intime", "");
                        datas.put("outtime", "");

                        //asynchronously write data
                        ApiFuture<WriteResult> result = docRef.set(datas);
                        JOptionPane.showMessageDialog(null, "Data has been inserted successfully!");
                    }
                    catch (Exception etc){
                        System.out.println(etc);
                    }
                }
            }
        });

        JButton btnUpload = new JButton("Upload Excel Workbook");

        btnUpload.setBackground(Color.magenta);
        btnUpload.setForeground(Color.BLUE);
        btnUpload.setBounds(180, 280, 180, 23);
        f.getContentPane().add(btnUpload);

        btnUpload.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    new ScheduleReader(db);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InvalidFormatException e1) {
                    e1.printStackTrace();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    e1.printStackTrace();
                }
            }
        });

        JButton btnDelete = new JButton("Delete Entry");

        btnDelete.setBackground(Color.magenta);
        btnDelete.setForeground(Color.BLUE);
        btnDelete.setBounds(400, 280, 120, 23);
        f.getContentPane().add(btnDelete);

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ApiFuture<QuerySnapshot> future =
                        db.collection("batch_status")
                                .whereEqualTo("batch_code", (String)(cb.getSelectedItem()))
                                .whereEqualTo("date", date.getText().toString()).get();
                // future.get() blocks on response
                List<QueryDocumentSnapshot> documents = null;
                try {
                    documents = future.get().getDocuments();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    e1.printStackTrace();
                }
                for (DocumentSnapshot document : documents) {
//                    System.out.println(document.getId() + " => " + document.get("university_name"));
                    id = document.getId();
//                    batch_name = (String)(cb.getSelectedItem());
//                    uni.setText((String) document.get("university_name"));
//                    trainer.setText((String) document.get("trainer_name"));
//                    startTime.setText((String) document.get("start"));
//                    endTime.setText((String) document.get("end"));
//                    status.setText((String) document.get("status"));
//                    date.setText((String) document.get("date"));
//                    day.setText((String) document.get("day"));
                    ApiFuture<WriteResult> writeResult = db.collection("batch_status").document(id).delete();

                }

            }
        });

        JButton btnDeleteAll = new JButton("Delete All");

        btnDeleteAll.setBackground(Color.magenta);
        btnDeleteAll.setForeground(Color.BLUE);
        btnDeleteAll.setBounds(540, 280, 100, 23);
        f.getContentPane().add(btnDeleteAll);

        btnDeleteAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ApiFuture<QuerySnapshot> future =
                        db.collection("batch_status").get();
                // future.get() blocks on response
                List<QueryDocumentSnapshot> documents = null;
                try {
                    documents = future.get().getDocuments();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                } catch (ExecutionException e1) {
                    e1.printStackTrace();
                }
                for (DocumentSnapshot document : documents) {
//                    System.out.println(document.getId() + " => " + document.get("university_name"));
                    id = document.getId();
//                    batch_name = (String)(cb.getSelectedItem());
//                    uni.setText((String) document.get("university_name"));
//                    trainer.setText((String) document.get("trainer_name"));
//                    startTime.setText((String) document.get("start"));
//                    endTime.setText((String) document.get("end"));
//                    status.setText((String) document.get("status"));
//                    date.setText((String) document.get("date"));
//                    day.setText((String) document.get("day"));
                    ApiFuture<WriteResult> writeResult = db.collection("batch_status").document(id).delete();
                }

            }
        });



    }
}
