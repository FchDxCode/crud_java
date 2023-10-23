package mdl10b;

//import tools yang di butuhkan
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Mdl10b extends JFrame{     //kita buat menggunakan JFrame dengan null layout seperti di soal modul8 no 1

    //pertama buat variable komponen
    private JTextField txtNim,txtNama;
    private JButton btnSimpan,btnHapus,btnCari,btnEdit,btnKeluar;
    private JComboBox<String> comboMatakuliah;
    private JRadioButton radioA,radioB,radioC;
    private ButtonGroup kelasGroup;
    private JTextArea hasilArea;    //ini untuk menampilkan hasil cari nya
    
    //variable untuk connect db
    private Connection conn;
    private Statement stmt;
    
    public Mdl10b() {
        //set up komponen gui dan ambil variable di atas
        setTitle("Crud Modul 10B");
        setLayout(null);
        
        JLabel lblNim = new JLabel("NIM:");
        lblNim.setBounds(20, 20, 80, 25);
        add(lblNim);
        
        txtNim = new JTextField();
        txtNim.setBounds(110, 20, 150, 25);
        add(txtNim);
        
        JLabel lblNama = new JLabel("Nama:");
        lblNama.setBounds(20, 60, 80, 25);
        add(lblNama);
        
        txtNama = new JTextField();
        txtNama.setBounds(110, 60, 150, 25); //coba run lagi
        add(txtNama);
        
        JLabel lblKelas  = new JLabel("Kelas:");
        lblKelas.setBounds(20, 100, 80, 25);
        add(lblKelas);
        
        radioA = new JRadioButton("A");
        radioA.setBounds(110, 100, 50, 25);
        add(radioA);
        
        radioB = new JRadioButton("B");
        radioB.setBounds(160, 100, 50, 25);
        add(radioB);
        
        radioC = new JRadioButton("C");
        radioC.setBounds(210, 100, 50, 25);
        add(radioC);
        
        kelasGroup = new ButtonGroup();
        kelasGroup.add(radioA);
        kelasGroup.add(radioB);
        kelasGroup.add(radioC);
        
        JLabel lblMatakuliah = new JLabel("Matakuliah:");
        lblMatakuliah.setBounds(20, 140, 80, 25);
        add(lblMatakuliah);
        
        String[] matakuliah = {"Kalkulus", "Matematika", "Fisika", "Kimia", "Biologi", "EthicalHacking"};
        comboMatakuliah = new JComboBox<>(matakuliah);
        comboMatakuliah.setBounds(110, 140, 150, 25);
        add(comboMatakuliah);
        
        //buat gui untuk button nya
        btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(20, 180, 80, 30);
        add(btnSimpan);
        
        btnEdit = new JButton("Edit");
        btnEdit.setBounds(110, 180, 80, 30);
        add(btnEdit);
        
        btnHapus = new JButton("hapus");
        btnHapus.setBounds(200, 180, 80, 30);
        add(btnHapus);
        
        btnCari = new JButton("Cari");
        btnCari.setBounds(20, 220, 80, 30);
        add(btnCari);
        
        btnKeluar = new JButton("Keluar");
        btnKeluar.setBounds(110, 220, 80, 30);
        add(btnKeluar);
        
        hasilArea = new JTextArea();
        hasilArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(hasilArea);
        scrollPane.setBounds(300, 20, 300, 230);
        add(scrollPane);
        
        //setup koneksi ke database huft...
       
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mdl10b", "root", "");
            stmt = conn.createStatement();
            System.err.println("Teknoneksi!");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Yah Gagal Konek wkwkwk");
            System.exit(1);
        } //block kode koneksi lalu tekan alt + enter cari suround with catch yang SQLexception
           
        //buat event untuk setiap button
        btnSimpan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //fungsi dari tombol simpan
                //ambil variable lokal yang di buat di atas tadi 
                String nim = txtNim.getText();
                String nama = txtNama.getText();
                String kelas = "";
                
                if (radioA.isSelected()) {
                    kelas = "A";
                } else if (radioB.isSelected()) {
                    kelas = "B";
                } else if (radioC.isSelected()) {
                    kelas = "C";
                }
                
                String matakuliah = (String) comboMatakuliah.getSelectedItem();
                
                //kita buat query sql untuk perikasa apakah nim sudah ada di dalam db berguna untuk menghindari duplikasi data
                String checkQuery = "SELECT nim FROM mahasiswa WHERE nim=? ";
                
                //persiapan statement sql untuk periksa
                try {
                    PreparedStatement checkStatement = conn.prepareStatement(checkQuery);
                    checkStatement.setString(1, nim);
                    
                    //eksekusi peryataan esqiel
                    ResultSet resultSet = checkStatement.executeQuery();
                    
                    //kita buat if untuk memberitahu user jika data dengan nim sudah ada
                    if (resultSet.next()) {
                        JOptionPane.showMessageDialog(null, "Data dengan Nim" + nim + "sudah di gunakan");
                    } else {
                        int Confirm = JOptionPane.showConfirmDialog(null, "Apakah anda yakin data sudah benar?", "Konfirmasi", JOptionPane.OK_CANCEL_OPTION);
                        
                        if (Confirm == JOptionPane.OK_OPTION) {
                            //jika pengguna mengklik ok maka data akan tersimpan kedalam Db
                            String insertQuery = "INSERT INTO mahasiswa(nim, nama, kelas, matakuliah) VALUES(?, ?, ?, ?)";
                            
                            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);
                            preparedStatement.setString(1, nim);
                            preparedStatement.setString(2, nama);
                            preparedStatement.setString(3, kelas);
                            preparedStatement.setString(4, matakuliah);
                            
                            //eksekusi es qi el
                            int rowsAffected = preparedStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                JOptionPane.showMessageDialog(null, "Data Berhasil di simpan");
                            } else {
                                JOptionPane.showMessageDialog(null, "Yahh data gagal di simpan");
                            }
                            
                            //reset inputan user jika simpan nya berhasil
                            txtNim.setText("");
                            txtNama.setText("");
                            kelasGroup.clearSelection();
                            comboMatakuliah.setSelectedIndex(0);
                        }
                    }
                    
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat menyimpan data");
                }
            }
        });
        
        //okeh lanjut untuk button edit 
        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //fungsi untuk tombol edit
                //ambil variable lokal lagi
                String nimToSearch = txtNim.getText();
                String nama = txtNama.getText();
                
                //kita buat quey sql yang hanya bisa mencari menggunakan nim saja
                String selectQuery = "SELECT * FROM mahasiswa WHERE nim=?";
                
                try {
                    //persiapkan statement esqiel untuk mencari data
                    PreparedStatement selectStatement = conn.prepareStatement(selectQuery);
                    selectStatement.setString(1, nimToSearch);
                    
                    //eksekusi si esqiel
                    ResultSet resulSet = selectStatement.executeQuery();
                    
                    if(resulSet.next()) {
                        //esqiel mengupdate berdasarkan nim
                        String updateQuery = "UPDATE mahasiswa SET nama=?, kelas=?, matakuliah=? WHERE nim=?";
                        
                        try {
                            //periapan statement sql untuk update data
                            PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
                            updateStatement.setString(1, nama);
                            
                            String kelas = "";
                            if (radioA.isSelected()) {
                                kelas = "A";
                            } else if (radioB.isSelected()) {
                                kelas = "B";
                            } else if (radioC.isSelected()) {
                                kelas = "C";
                            }
                            updateStatement.setString(2, kelas);
                            updateStatement.setString(3, (String) comboMatakuliah.getSelectedItem());
                            updateStatement.setString(4, nimToSearch);
                            
                            //eksekusi es qi el untuk update data
                            int rowsAffected = updateStatement.executeUpdate();
                            if (rowsAffected > 0) {
                                JOptionPane.showMessageDialog(null, "Data dengan Nim" + nimToSearch + "Berhasil di ubah");
                            } else {
                                JOptionPane.showMessageDialog(null, "Data dengan Nim" + nimToSearch + "Gagal diubah atau tidak di temukan");
                            }
                            
                            //reset inputan jika berhasil edit
                            txtNim.setText("");
                            txtNama.setText("");
                            kelasGroup.clearSelection();
                            comboMatakuliah.setSelectedIndex(0);
                        } catch(SQLException ex){
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(null, "Terjadi sebuah kesalahan saat edit data");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Data dengan Nim" + nimToSearch + "tidak di temukan");
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat mencari data");
                }
            }
        });
        
        //nah lanjut untuk event hapus 
        btnHapus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //fungsi untuk hapus nyaa
                //ambil variable lokal nya dulu
                String nimToDelete = txtNim.getText();
                
                //query esqiel untuk hapus data
                String deleteQuery = "DELETE FROM mahasiswa WHERE nim=?"; //kita menghapus data hanya bisa menggunakan nim saja ya
                
                try {
                    //persiapan statement esqiel
                    PreparedStatement preparedStatement =conn.prepareStatement(deleteQuery);
                    preparedStatement.setString(1, nimToDelete);
                    
                    //eksekusi sql
                    int rowsAffected = preparedStatement.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Data dengan Nim" + nimToDelete + "berhasil di hapus");
                    } else {
                        JOptionPane.showMessageDialog(null, "Data dengan Nim" + nimToDelete + "tidak di temukan atau gagal di hapus");
                    }
                    
                    //reset inputan lagi
                    txtNim.setText("");
                    txtNama.setText("");
                    kelasGroup.clearSelection();
                    comboMatakuliah.setSelectedIndex(0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat hapus data");
                } 
            }
        });
        
        //okehh lanjut untuk event cari agak sedikit berbeda
        btnCari.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //fungsi untuk cari
                //pertama ambil lokal variable seperti biasa
                String nimToSearch = txtNim.getText().trim();
                
                //kita akan buat sebuah kata kunci yang mana saat kata kunci tersebut di pakai akan menampilkan semua data yang ada di database
                String selectQuery = "SELECT * FROM mahasiswa ORDER BY nama ASC";
                
                if (!nimToSearch.isEmpty() && !nimToSearch.equals("semua")) { //disini key nya adalah semua ya
                    selectQuery = "SELECT * FROM mahasiswa WHERE nim LIKE ? ORDER BY nama ASC";
                }
                
                try {
                    //persiapan statement sql
                    PreparedStatement preparedStatemen = conn.prepareStatement(selectQuery);
                    
                    if (!nimToSearch.isEmpty() && !nimToSearch.equals("semua")) {
                        preparedStatemen.setString(1, "%" + nimToSearch + "%%"); //kita menggunakan LIKE untuk menggunakan kata kunci
                    }
                    
                    //eksekusi esqiel
                    ResultSet resultSet = preparedStatemen.executeQuery();
                    
                    //untuk menampilkan hasil cari di areaHasil
                    StringBuilder hasilPencarian = new StringBuilder();
                    int counter = 0; //untuk menambah counter 
                    
                    while (resultSet.next()) {
                        String nim = resultSet.getString("nim");
                        String nama = resultSet.getString("nama");
                        String kelas = resultSet.getString("kelas");
                        String matakuliah = resultSet.getString("matakuliah");
                        
                        if (nimToSearch.equals("semua")) {
                            counter++; //kita buat agar data yang tampil di beri nomor
                            hasilPencarian.append("Nomor:").append(counter).append("\n");
                        }
                        
                        hasilPencarian.append("NIM:").append(nim).append("\n");
                        hasilPencarian.append("Nama:").append(nama).append("\n");
                        hasilPencarian.append("Kelas:").append(kelas).append("\n");
                        hasilPencarian.append("Matakuliah:").append(matakuliah).append("\n\n");
                    }
                    
                    if(hasilPencarian.length() > 0) {
                        hasilArea.setText(hasilPencarian.toString()); //konversi menjadi string
                    } else {
                        JOptionPane.showMessageDialog(null, "Data dengan Nim" + nimToSearch + "tidak di temukan");
                        hasilArea.setText(""); //untuk hapus hasil pencarian
                    }
                    //kita buat untuk reset hasil cari jika ketemu
                    txtNim.setText(""); //oh iya untuk menggunakan kata kunci semua di gunakan di kolom NIM ya
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Terjadi kesalahan saat mencari data");
                }
            }
        });
        
        //okehh event untuk button keluar
        btnKeluar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //fungsi untuk tombol keluar
                int Confirm = JOptionPane.showConfirmDialog(null, "Apakah anda yakin ingin keluar?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
                
                if (Confirm == JOptionPane.YES_OPTION) {
                    //sekalian tutup database
                    try {
                        if (stmt != null) {
                            stmt.close();
                        }
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                    //keluar dari GUI
                    System.exit(0);
                }
            }
        });
        
        //selesai bikin event dan fungsi dan langkah terakhir
        
        setSize(640, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
        
        //okeh coba di run, seperti nya di form nama ada yang salah
    }
    
   public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Mdl10b();
            }
        });
    }
    
}
