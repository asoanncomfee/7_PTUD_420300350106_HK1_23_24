/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import dao.ChiTietHoaDonDAO;
import dao.HoaDonDAO;
import dao.KhachHangDAO;
import dao.KhuyenMaiDAO;
import dao.KichThuocDAO;
import dao.MauSacDAO;
import dao.NhanVienDAO;
import dao.PhanLoaiDAO;
import dao.SanPhamDAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.KichThuoc;
import entity.MauSac;
import entity.NhanVien;
import entity.PhanLoai;
import entity.SanPham;

/**
 *
 * @author ASUS
 */
public class TabBanHang extends javax.swing.JPanel {

    /**
     * Creates new form TabBanHang
     */
	private static KhachHang khachHang = null;
    private static SanPham sanPham = null;
    public static ArrayList<ChiTietHoaDon> listChiTietHoaDon = new ArrayList<>();
    private static boolean isThemKhachHangActive = false;
    public static boolean isThanhToan = false;

    private static final SanPhamDAO sanPham_DAO = new SanPhamDAO();
    private final PhanLoaiDAO phanLoai_DAO = new PhanLoaiDAO();
    private final MauSacDAO mauSac_DAO = new MauSacDAO();
    private final KichThuocDAO kichThuoc_DAO = new KichThuocDAO();
    private final KhachHangDAO khachHang_DAO = new KhachHangDAO();
    private static final HoaDonDAO hoaDon_DAO = new HoaDonDAO();
    private static final ChiTietHoaDonDAO cthd_DAO = new ChiTietHoaDonDAO();
    private final NhanVienDAO nv_DAO = new NhanVienDAO();
    private KhuyenMaiDAO khuyenMai_DAO;
    private final NhanVien nhanVien = Login.nhanVien;
    private static boolean isKhachHang = false;
	
    public TabBanHang() {
        initComponents();
        initialValue();
        designTable();
        tblCTHD_changeHandler();
        setEvent();
    }
    
    private void designTable() {
        tbl_danhSachSanPham.getTableHeader().setFont(new java.awt.Font("Calibri", 0, 12));
        tbl_danhSachSanPham.getTableHeader().setOpaque(false);
        tbl_danhSachSanPham.getTableHeader().setBackground(new Color(198, 226, 255));
        tbl_danhSachSanPham.getTableHeader().setForeground(new Color(16,54,103));
        tbl_danhSachSanPham.setDefaultEditor(Object.class, null); 


        tbl_chiTietHoaDon.getTableHeader().setFont(new java.awt.Font("Calibri", 0, 12));
        tbl_chiTietHoaDon.getTableHeader().setOpaque(false);
        tbl_chiTietHoaDon.getTableHeader().setBackground(new Color(198, 226, 255));
        tbl_chiTietHoaDon.getTableHeader().setForeground(new Color(16,54,103));
        tbl_chiTietHoaDon.setDefaultEditor(Object.class, null); 

    }

    private void initialValue() {
        ArrayList<MauSac> listMauSac = mauSac_DAO.getAllMauSac();

        for (MauSac ms : listMauSac) {
            cb_mauSac.addItem(ms.getMauSac());
        }

        ArrayList<PhanLoai> listPhanLoai = phanLoai_DAO.getAllPhanLoai();

        for (PhanLoai pl : listPhanLoai) {
            cb_loaiSanPham.addItem(pl.getLoaiSanPham());
        }

        ArrayList<KichThuoc> listKichThuoc = kichThuoc_DAO.getAllKichThuoc();

        for (KichThuoc kt : listKichThuoc) {
            cb_kichCo.addItem(kt.getKichThuoc());
        }

        tblDanhSachSanPhamWithFilter();
    }

    public static void resetTblDanhSachSP() {
        ArrayList<SanPham> listSP = sanPham_DAO.getAllSanPham();

        removeSelectedDSSP();
        clearTableDSSP();
        DefaultTableModel dtm = (DefaultTableModel) tbl_danhSachSanPham.getModel();
        for (SanPham sp : listSP) {
            String giaBan = "";
            if (sp.getKhuyenMai().getSoLuong() > 0) {
                giaBan = NumberFormat.getInstance().format(sp.tinhGiaBanLucSau());
            } else {
                giaBan = NumberFormat.getInstance().format(sp.tinhGiaBanLucDau());
            }
            Object[] rowData = {sp.getMaSP(), sp.getTenSP(), sp.getPhanLoai().getLoaiSanPham(), giaBan, sp.getSoLuong(), sp.getMauSac().getMauSac(), sp.getChatLieu().getChatLieu(), sp.getKichThuoc().getKichThuoc()};
            dtm.addRow(rowData);
        }
    }

    private static void tblDanhSachSanPham() {
        removeSelectedDSSP();
        clearTableDSSP();
        DefaultTableModel dtm = (DefaultTableModel) tbl_danhSachSanPham.getModel();

        ArrayList<SanPham> listSanPham = sanPham_DAO.getAllSanPham();
        for (SanPham sp : listSanPham) {
            String giaBan = "";
            if (sp.getKhuyenMai().getSoLuong() > 0) {
                giaBan = NumberFormat.getInstance().format(sp.tinhGiaBanLucSau());
            } else {
                giaBan = NumberFormat.getInstance().format(sp.tinhGiaBanLucDau());
            }
            Object[] rowData = {sp.getMaSP(), sp.getTenSP(), sp.getPhanLoai().getLoaiSanPham(), giaBan, sp.getSoLuong(), sp.getMauSac().getMauSac(), sp.getChatLieu().getChatLieu(), sp.getKichThuoc().getKichThuoc()};
            dtm.addRow(rowData);
        }
    }

    private static void tblDanhSachSanPhamWithFilter() {
        removeSelectedDSSP();
        clearTableDSSP();
        DefaultTableModel dtm = (DefaultTableModel) tbl_danhSachSanPham.getModel();

        String maSanPham = txt_maSanPham.getText();
        String tenSanPham = txt_tenSanPham.getText();
        String loaiSanPham = (String) cb_loaiSanPham.getSelectedItem();
        if (cb_loaiSanPham.getSelectedItem().equals("Tất cả")) {
            loaiSanPham = "";
        }

        String mauSac = (String) cb_mauSac.getSelectedItem();
        if (cb_mauSac.getSelectedItem().equals("Tất cả")) {
            mauSac = "";
        }

        String kichCo = (String) cb_kichCo.getSelectedItem();
        if (cb_kichCo.getSelectedItem().equals("Tất cả")) {
            kichCo = "";
        }

        ArrayList<SanPham> listSanPham = sanPham_DAO.getAllSanPham(maSanPham, tenSanPham, loaiSanPham, mauSac, kichCo);

        for (SanPham sp : listSanPham) {
            String giaBan = "";
            if (sp.getKhuyenMai().getSoLuong() > 0) {
                giaBan = NumberFormat.getInstance().format(sp.tinhGiaBanLucSau());
            } else {
                giaBan = NumberFormat.getInstance().format(sp.tinhGiaBanLucDau());
            }
            Object[] rowData = {sp.getMaSP(), sp.getTenSP(), sp.getPhanLoai().getLoaiSanPham(), giaBan, sp.getSoLuong(), sp.getMauSac().getMauSac(), sp.getChatLieu().getChatLieu(), sp.getKichThuoc().getKichThuoc()};
            dtm.addRow(rowData);
        }
    }

    private static void clearTableDSSP() {
        DefaultTableModel dtm1 = (DefaultTableModel) tbl_danhSachSanPham.getModel();
        dtm1.setRowCount(0);
    }

    private static void clearTable() {
        DefaultTableModel dtm = (DefaultTableModel) tbl_chiTietHoaDon.getModel();
        dtm.setRowCount(0);
    }

    private static void removeSelectedDSSP() {
        tbl_danhSachSanPham.clearSelection();
    }

    private static void tblChiTietHoaDon() {
        clearTable();
        DefaultTableModel dtm = (DefaultTableModel) tbl_chiTietHoaDon.getModel();

        for (ChiTietHoaDon cthd : listChiTietHoaDon) {
            long giaBan = 0;
            if (cthd.getSanPham().getKhuyenMai().getSoLuong() > 0) {
                giaBan = cthd.getSanPham().tinhGiaBanLucSau();
            } else {
                giaBan = cthd.getSanPham().tinhGiaBanLucDau();
            }
            Object[] dataRow = {cthd.getSanPham().getMaSP(), cthd.getSanPham().getTenSP(), giaBan, cthd.getSoLuong()};
            dtm.addRow(dataRow);
        }
    }

    private boolean isThemValid() {
        
        if (sanPham == null) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn sản phẩm để thêm vào hoá đơn");
            return false;
        }

        if (txt_soLuong.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số lượng");
            return false;
        }
       

        try {
            int soLuong = Integer.parseInt(txt_soLuong.getText());

            if (soLuong < 1) {
                JOptionPane.showMessageDialog(null, "Số lượng phải từ 1 trở lên");
                return false;
            }

            if (soLuong > sanPham.getSoLuong()) {
                JOptionPane.showMessageDialog(null, "Số lượng phải bé hơn số hàng tồn kho");
                return false;
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null, "Số lượng phải là 1 con số lớn hơn 0");
            return false;
        }
        return true;
    }

    private static void tongTienHandler() {

    	long tongTien = 0;


        for (int i = 0; i < tbl_chiTietHoaDon.getRowCount(); i++) {
            long donGia = Long.parseLong(tbl_chiTietHoaDon.getValueAt(i, 3).toString());
            int soLuong = Integer.parseInt(tbl_chiTietHoaDon.getValueAt(i, 2).toString());

            long thanhTien = donGia * soLuong;
            tongTien += thanhTien;
        }

       

        lbl_thanhTienValue.setText(tongTien + "");
        
    }
    
    private static void tienThueHandler() {
    	long tongTien = 0;
    	long thue = 0;
    	double phanTramThue = 0.015; //1.5%
    	 for (int i = 0; i < tbl_chiTietHoaDon.getRowCount(); i++) {
             long donGia = Long.parseLong(tbl_chiTietHoaDon.getValueAt(i, 3).toString());
             int soLuong = Integer.parseInt(tbl_chiTietHoaDon.getValueAt(i, 2).toString());

             long thanhTien = donGia * soLuong;
             tongTien += thanhTien;
         }
    	 
    	 thue = (long) (tongTien * phanTramThue);
    	 
    	 lbl_thueValue.setText(thue + "");
       
    	 
    }
    
    private static void tongTienSauThueHandler() {

    	long tongTien = 0;
    	
    	double thue = 0;
    	double phanTramThue = 0.015; //1.5%


        for (int i = 0; i < tbl_chiTietHoaDon.getRowCount(); i++) {
            long donGia = Long.parseLong(tbl_chiTietHoaDon.getValueAt(i, 3).toString());
            int soLuong = Integer.parseInt(tbl_chiTietHoaDon.getValueAt(i, 2).toString());

            long thanhTien = donGia * soLuong;
            tongTien += thanhTien;
        }

        thue = tongTien * phanTramThue;
        tongTien += thue;
   	 

        lbl_tongTienValue.setText(tongTien + "");
        
    }

    private static boolean tonTaiSanPhamTrongCTHD(SanPham sp) {

        if (tbl_chiTietHoaDon.getRowCount() < 1) {
            return false;
        }

        for (int i = 0; i < tbl_chiTietHoaDon.getRowCount(); i++) {
            if (tbl_chiTietHoaDon.getValueAt(i, 0).equals(sp.getMaSP())) {
                int soLuong = Integer.parseInt(txt_soLuong.getText());

                int tongSP = Integer.parseInt(tbl_chiTietHoaDon.getValueAt(i, 3).toString()) + soLuong;

                tbl_chiTietHoaDon.setValueAt(tongSP, i, 3);
                return true;
            }
        }
        return false;
    }

    private static void capNhatSLSPKhiThemCTHD(SanPham sp, int soLuong) {
        sp.setSoLuong(sp.getSoLuong() - soLuong);
        if (sanPham_DAO.capNhatSoLuong(sp) == -1) {
            return;
        }
    }

    private static void themCTHDHandler() {
        int soLuong = Integer.parseInt(txt_soLuong.getText());

        String maSP = (String) tbl_danhSachSanPham.getValueAt(tbl_danhSachSanPham.getSelectedRow(), 0);
        SanPham sp = sanPham_DAO.getSanPhamById(maSP);

        
        int soLuongKhuyenMai = sp.getKhuyenMai().getSoLuong();
        Date ngayBD = new java.sql.Date(sp.getKhuyenMai().getNgayBatDau().getTime());
        Date ngayHT = new java.sql.Date(System.currentTimeMillis()); 
        
        if (soLuong > soLuongKhuyenMai && soLuongKhuyenMai > 0 && (ngayHT.toString().equals(ngayBD.toString()) || ngayHT.after(ngayBD))) {
            if (JOptionPane.showConfirmDialog(null, "Sản phẩm bạn chọn chỉ còn " + soLuongKhuyenMai + " sản phẩm khuyến mãi. Bạn có muốn tiếp tục mua không", "Confirm", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                capNhatSLSPKhiThemCTHD(sp, soLuong);

                DefaultTableModel dtm = (DefaultTableModel) tbl_chiTietHoaDon.getModel();

                if (!tonTaiSanPhamTrongCTHD(sp)) {
                    long giaBan = 0;
                    if (sp.getKhuyenMai().getSoLuong() > 0) {
                        giaBan = sp.tinhGiaBanLucSau();
                    } else {
                        giaBan = sp.tinhGiaBanLucDau();
                    }
                    Object[] rowData = {sp.getMaSP(), sp.getTenSP(), (giaBan *soLuongKhuyenMai + sp.tinhGiaBanLucDau()* (soLuong-soLuongKhuyenMai))/soLuong, soLuong, sp.getPhanLoai().getLoaiSanPham(), sp.getMauSac().getMauSac(), sp.getChatLieu().getChatLieu(), sp.getKichThuoc().getKichThuoc()};
    
                    dtm.addRow(rowData);
                    
                }

                txt_soLuong.setText("");
                tongTienHandler();
                tienThueHandler();
                tongTienSauThueHandler();
                tblDanhSachSanPhamWithFilter();
                sanPham = null;
            }
        }
        else{
              capNhatSLSPKhiThemCTHD(sp, soLuong);

                DefaultTableModel dtm = (DefaultTableModel) tbl_chiTietHoaDon.getModel();

                if (!tonTaiSanPhamTrongCTHD(sp)) {
                    long giaBan = 0;
                    if (sp.getKhuyenMai().getSoLuong() > 0) {
                        giaBan = sp.tinhGiaBanLucSau();
                    } else {
                        giaBan = sp.tinhGiaBanLucDau();
                    }
                    Object[] rowData = {sp.getMaSP(), sp.getTenSP(), giaBan, soLuong, sp.getPhanLoai().getLoaiSanPham(), sp.getMauSac().getMauSac(), sp.getChatLieu().getChatLieu(), sp.getKichThuoc().getKichThuoc()};
                    dtm.addRow(rowData);
                }

                txt_soLuong.setText("");
                tongTienHandler();
                tienThueHandler();
                tongTienSauThueHandler();
                tblDanhSachSanPhamWithFilter();
                sanPham = null;
        }


    }

    private boolean isThemHangChoValid() {
        if (tbl_chiTietHoaDon.getRowCount() < 1) {
            JOptionPane.showMessageDialog(null, "Vui lòng thêm sản phẩm.");
            return false;
        }

        if (txt_soDienThoai.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Vui lòng điền đầy đủ thông tin khách hàng.");
            return false;
        }

        if (txt_tenKhachHang.getText().equals("") || txt_gmail.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập thông tin khách hàng");
            return false;
        }

        return true;
    }

    private boolean isValidInput() {
        if (txt_soDienThoai.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập số điện thoại để tìm kiếm");
            return false;
        }

        return true;
    }

    private void clearInput() {
        txt_soDienThoai.setText("");
        txt_tenKhachHang.setText("");
        txt_tienKhachDua.setText("");
        txt_gmail.setText("");
        lbl_tongTienValue.setText("0");
        lbl_thueValue.setText("");
        lbl_thanhTienValue.setText("0");
        lbl_tienThuaValue.setText("0");
    }

    private static void capNhatSLSPKhiXoaSP(SanPham sp, int soLuong) {
        sp.setSoLuong(sp.getSoLuong() + soLuong);
        if (sanPham_DAO.capNhatSoLuong(sp) == -1) {
            return;
        }
    }

    private static void xoaSanPhamHandler() {
        if (tbl_chiTietHoaDon.getSelectedRow() == -1) {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn sản phẩm muốn xoá");
            return;
        }

        SanPham sp = sanPham_DAO.getSanPhamById((String) tbl_chiTietHoaDon.getValueAt(tbl_chiTietHoaDon.getSelectedRow(), 0));

        int soLuong = (int) tbl_chiTietHoaDon.getValueAt(tbl_chiTietHoaDon.getSelectedRow(), 3);

        capNhatSLSPKhiXoaSP(sp, soLuong);

        DefaultTableModel dtm = (DefaultTableModel) tbl_chiTietHoaDon.getModel();
        dtm.removeRow(tbl_chiTietHoaDon.getSelectedRow());

        
        tblDanhSachSanPham();
        tongTienHandler();
        tienThueHandler();
        tongTienSauThueHandler();
    }

    private void enableFormKhachHang() {
        txt_gmail.setEnabled(true);
        txt_tenKhachHang.setEnabled(true);
        txt_gioiTinh.setEnabled(true);
    }

    private static void disableFormKhachHang() {
        txt_gmail.setEnabled(false);
        txt_tenKhachHang.setEnabled(false);
        txt_gioiTinh.setEnabled(false);
    }

    private static void thongTinKhachHang(KhachHang khachHang) {
        txt_soDienThoai.setText(khachHang.getSdt());
        txt_gmail.setText(khachHang.getEmail());
        txt_tenKhachHang.setText(khachHang.getHoVaTen());
        txt_gioiTinh.setText(khachHang.getGioiTinh() ? "Nữ" : "Nam");
    }

    private void btnTimHandler() {
        if (!isSoDienThoaiValid()) {
            return;
        }

        khachHang = khachHang_DAO.getKhachHangBySdt(txt_soDienThoai.getText().trim());
        if (khachHang == null) {
            if (JOptionPane.showConfirmDialog(null, "Không tìm thấy khách hàng. Bạn có muốn thêm thông tin khách hàng không?", "Thêm khách hàng", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                TrangChu.activeQLKHForm();
            }
            JOptionPane.showMessageDialog(null, "Không tìm thấy số điện thoại vui lòng nhập thông tin khách hàng mới cho số điện thoại này");
          enableFormKhachHang();
          txt_tenKhachHang.setText("");
          txt_gioiTinh.setText("");
          txt_gmail.setText("");
          isThemKhachHangActive = true;
        } else {
            thongTinKhachHang(khachHang);
            isThemKhachHangActive = false;
            isKhachHang = true;
        }
    }

    private boolean isThanhToanHopLe() {
        if (tbl_chiTietHoaDon.getRowCount() < 1) {
            JOptionPane.showMessageDialog(null, "Vui lòng thêm sản phẩm để thanh toán");
            return false;
        }

        if (txt_tienKhachDua.getText().equals("")) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập tiền khách hàng đưa");
            return false;
        }
        if (!isKhachHang) {
            JOptionPane.showMessageDialog(null, "Vui lòng lấy thông tin khách hàng");
            return false;
        }

        try {
            long tienKhachDua = Long.parseLong(txt_tienKhachDua.getText());
         
            long tongTien = Long.parseLong(lbl_tongTienValue.getText());

            if (tienKhachDua - tongTien < 0) {
                JOptionPane.showMessageDialog(null, "Không đủ tiền thanh toán.");
                return false;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Tiền khách hàng đưa phải là chữ số");
            return false;
        }

        return true;
    }

    private KhachHang layThongTinKhachHang() {
        String tenKhachHang = txt_tenKhachHang.getText();
        String gmail = txt_gmail.getText();
        String sdt = txt_soDienThoai.getText();
        boolean gioiTinh = txt_gioiTinh.getText().equals("Nữ") ? true : false;
        
       return new KhachHang(tenKhachHang, gmail, sdt, gioiTinh);
    }

    private static void khoiTaoGiaTri() {
        listChiTietHoaDon.clear();

        khachHang = null;
        isThemKhachHangActive = false;
        lblHinhAnh.setIcon(null);
        tblDanhSachSanPhamWithFilter();
        tblChiTietHoaDon();
        isKhachHang = false;
    }

    private void thanhToanHandler() {
        if (isThemKhachHangActive) {
            if (!isThongTinKhachHangValid()) {
                return;
            }

            khachHang = layThongTinKhachHang();
            if (khachHang_DAO.addKhachHang(khachHang) == -1) {
                return;
            }
            isThemKhachHangActive = false;
        } else {
            isThemKhachHangActive = false;
        }

        HoaDon hd = new HoaDon();

        KhachHang kh = khachHang_DAO.getKhachHangBySdt(txt_soDienThoai.getText());
        hd.setKhachHang(kh);
        hd.setNhanVien(nhanVien);
        Date currentDate = new Date(Calendar.getInstance().getTime().getTime());
        hd.setNgayLap(currentDate);

        if (hoaDon_DAO.addHoaDon(hd) == -1) {
            return;
        }

        ArrayList<ChiTietHoaDon> listChiTietHoaDon = new ArrayList<>();

        for (int i = 0; i < tbl_chiTietHoaDon.getRowCount(); i++) {
            int soLuong = Integer.parseInt(tbl_chiTietHoaDon.getValueAt(i, 3).toString());
            double donGia = Long.parseLong(tbl_chiTietHoaDon.getValueAt(i, 2).toString());
            SanPham sp = sanPham_DAO.getSanPhamById(tbl_chiTietHoaDon.getValueAt(i, 0).toString());

            double thanhTien = donGia * soLuong;
            double thue =  thanhTien * 0.015;
            double tongTienHD = thanhTien + thue;
            
            ChiTietHoaDon cthd = new ChiTietHoaDon(sp, hd, soLuong, thanhTien, thue, tongTienHD);

            listChiTietHoaDon.add(cthd);
            if (cthd_DAO.addChiTietHoaDon(cthd) == -1) {
                return;
            }
        }

        capNhatSoLuongKhuyenMai();

        DefaultTableModel dtm = (DefaultTableModel) tbl_chiTietHoaDon.getModel();
        dtm.setRowCount(0);

        xuatHoaDon(hd, listChiTietHoaDon, kh, nhanVien);
        clearInput();
        JOptionPane.showMessageDialog(null, "Thanh toán thành công");
        khoiTaoGiaTri();
        disableFormKhachHang();
        openHoaDon(hd.getMaHoaDon());
    }

    private void xoaTatCaHandler() {
        if (tbl_chiTietHoaDon.getRowCount() < 1) {
            return;
        }

        for (int i = 0; i < tbl_chiTietHoaDon.getRowCount(); i++) {
            SanPham sp = sanPham_DAO.getSanPhamById((String) tbl_chiTietHoaDon.getValueAt(i, 0));
            int soLuong = Integer.parseInt(tbl_chiTietHoaDon.getValueAt(i, 3).toString());
            capNhatSLSPKhiXoaSP(sp, soLuong);
        }

        DefaultTableModel dtm = (DefaultTableModel) tbl_chiTietHoaDon.getModel();
        dtm.setRowCount(0);

        tblDanhSachSanPham();
        tongTienHandler();
        tienThueHandler();
        tongTienSauThueHandler();

    }

    public void capNhatSoLuongKhuyenMai() {
        khuyenMai_DAO = new KhuyenMaiDAO();
        DefaultTableModel dtm = (DefaultTableModel) tbl_chiTietHoaDon.getModel();
        ArrayList<SanPham> listSanPham = sanPham_DAO.getAllSanPham();
        for (int i = 0; i < tbl_chiTietHoaDon.getRowCount(); i++) {
            for (SanPham sanPham : listSanPham) {
                if (String.valueOf(dtm.getValueAt(i, 0)).equalsIgnoreCase(sanPham.getMaSP())) {
                    if (sanPham.getKhuyenMai().getPhanTramKhuyenMai() > 0) {
                        sanPham.getKhuyenMai().setSoLuong(sanPham.getKhuyenMai().getSoLuong() - Integer.parseInt(dtm.getValueAt(i, 3).toString()));
                        if (sanPham.getKhuyenMai().getSoLuong() >= 0) {
                            khuyenMai_DAO.updateKhuyenMai(sanPham.getKhuyenMai());
                        } else {
                            sanPham.getKhuyenMai().setSoLuong(Integer.parseInt(0 + ""));
                            khuyenMai_DAO.updateKhuyenMai(sanPham.getKhuyenMai());
                            sanPham.getKhuyenMai().setMaKhuyenMai("KM0001");
                            sanPham_DAO.updateSanPham(sanPham);
                        }

                    }
                }
            }
        }

    }

    public static void thanhToanHangCho(HoaDon hd) {

        ArrayList<ChiTietHoaDon> listCTHD = cthd_DAO.getAllCTHDByHoaDon(hd);

        if (cthd_DAO.deleteCTHD(hd) == -1) {
            return;
        }

        clearTable();
        DefaultTableModel dtm = (DefaultTableModel) tbl_chiTietHoaDon.getModel();

        for (int i = 0; i < listCTHD.size(); i++) {
            SanPham sp = listCTHD.get(i).getSanPham();
            long giaBan = 0;
            if (sp.getKhuyenMai().getSoLuong() > 0) {
                giaBan = sp.tinhGiaBanLucSau();
            } else {
                giaBan = sp.tinhGiaBanLucDau();
            }
            Object[] rowData = {sp.getMaSP(), sp.getTenSP(), giaBan, listCTHD.get(i).getSoLuong(), sp.getPhanLoai().getLoaiSanPham(),
                sp.getMauSac().getMauSac(), sp.getChatLieu().getChatLieu(), sp.getKichThuoc().getKichThuoc()};
            dtm.addRow(rowData);
        }

        KhachHang kh = hd.getKhachHang();

        if (hoaDon_DAO.deleteHoaDon(hd) == -1) {
            return;
        }

        thongTinKhachHang(kh);
        disableFormKhachHang();
        tongTienHandler();
        tienThueHandler();
        tongTienSauThueHandler();
        isKhachHang = true;
    }

    private void tienThuaHandler() {
        try {
            long tongTien = Long.parseLong(lbl_tongTienValue.getText());
            long tienKh = Long.parseLong(txt_tienKhachDua.getText());

            long tienThua = tienKh - tongTien;
            lbl_tienThuaValue.setText(NumberFormat.getInstance().format(tienThua));
        } catch (Exception e) {
        }

    }

    private boolean isSoDienThoaiValid() {
        if (!txt_soDienThoai.getText().matches("^0\\d{9}$")) {
            JOptionPane.showMessageDialog(null, "Số điện thoại không hợp lệ");

            return false;
        }

        return true;
    }

    private boolean isThongTinKhachHangValid() {
        if (txt_gmail.getText().trim().equals("") || txt_tenKhachHang.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Vui lòng nhập đầy đủ thông tin khách hàng");
            return false;
        }

        if (!txt_gmail.getText().trim().matches("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")) {
            JOptionPane.showMessageDialog(null, "Gmail khách hàng không hợp lệ");
            return false;

        }

        return true;
    }

    private void giamSoLuongHandler() {
        try {

        	 int selectedRow = tbl_chiTietHoaDon.getSelectedRow();
             if (selectedRow == -1) {
                 JOptionPane.showMessageDialog(null, "Vui lòng chọn một sản phẩm để giảm số lượng");
                 return;
             }

             int soLuongCoSan = Integer.parseInt(tbl_chiTietHoaDon.getValueAt(selectedRow, 3).toString());
             int soLuongGiam = 0;

             // Kiểm tra xem người dùng đã nhập số lượng từ TextField hay chưa
             if (!txt_soLuong.getText().isEmpty()) {
                 soLuongGiam = Integer.parseInt(txt_soLuong.getText());
             } else {
                 soLuongGiam = 1; // Nếu không nhập số lượng, mặc định giảm 1
             }

             if (soLuongGiam < 1) {
                 JOptionPane.showMessageDialog(null, "Số lượng phải lớn hơn 0");
                 return;
             }

             if (soLuongGiam > soLuongCoSan) {
                 JOptionPane.showMessageDialog(null, "Số lượng giảm phải bé hơn hoặc bằng số lượng hiện có");
                 return;
             }

             // Lấy thông tin sản phẩm
             SanPham spp = sanPham_DAO.getSanPhamById(tbl_chiTietHoaDon.getValueAt(selectedRow, 0).toString());
             if (spp == null) {
                 JOptionPane.showMessageDialog(null, "Không thể tìm thấy sản phẩm");
                 return;
             }

             spp.setSoLuong(spp.getSoLuong() - soLuongGiam);

             if (sanPham_DAO.capNhatSoLuong(spp) != -1) {
//                 JOptionPane.showMessageDialog(null, "Giảm số lượng thành công");

                 int soLuongSauCapNhat = soLuongCoSan - soLuongGiam;
                 tbl_chiTietHoaDon.setValueAt(soLuongSauCapNhat, selectedRow, 3);
                 tblDanhSachSanPhamWithFilter();
                 tongTienHandler();
                 tienThueHandler();
                 tongTienSauThueHandler();
                 txt_soLuong.setText("");

                 if (soLuongSauCapNhat == 0) {
                     DefaultTableModel model_CTHD = (DefaultTableModel) tbl_chiTietHoaDon.getModel();
                     model_CTHD.removeRow(selectedRow);
                 }
             }
         } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(null, "Số lượng phải là một số nguyên");
         } catch (Exception ex) {
             JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi giảm số lượng");
         }
    }

    private void tangSoLuongHandler() {
        try {

        	 int selectedRow = tbl_chiTietHoaDon.getSelectedRow();
             if (selectedRow == -1) {
                 JOptionPane.showMessageDialog(null, "Vui lòng chọn một sản phẩm để tăng số lượng");
                 return;
             }

             int soLuongCoSan = Integer.parseInt(tbl_chiTietHoaDon.getValueAt(selectedRow, 3).toString());
             int soLuongTang = 0;

             // Kiểm tra xem người dùng đã nhập số lượng từ TextField hay chưa
             if (!txt_soLuong.getText().isEmpty()) {
                 soLuongTang = Integer.parseInt(txt_soLuong.getText());
             } else {
                 soLuongTang = 1; // Nếu không nhập số lượng, mặc định tăng 1
             }

             if (soLuongTang < 1) {
                 JOptionPane.showMessageDialog(null, "Số lượng phải lớn hơn 0");
                 return;
             }

             // Lấy thông tin sản phẩm
             SanPham spp = sanPham_DAO.getSanPhamById(tbl_chiTietHoaDon.getValueAt(selectedRow, 0).toString());
             if (spp == null) {
                 JOptionPane.showMessageDialog(null, "Không thể tìm thấy sản phẩm");
                 return;
             }

             spp.setSoLuong(spp.getSoLuong() + soLuongTang);

             if (sanPham_DAO.capNhatSoLuong(spp) != -1) {
//                 JOptionPane.showMessageDialog(null, "Tăng số lượng thành công");

                 int soLuongSauCapNhat = soLuongCoSan + soLuongTang;
                 tbl_chiTietHoaDon.setValueAt(soLuongSauCapNhat, selectedRow, 3);
                 tblDanhSachSanPhamWithFilter();
                 tongTienHandler();
                 tienThueHandler();
                 tongTienSauThueHandler();
                 txt_soLuong.setText("");
             }
         } catch (NumberFormatException e) {
             JOptionPane.showMessageDialog(null, "Số lượng phải là một số nguyên");
         } catch (Exception ex) {
             JOptionPane.showMessageDialog(null, "Có lỗi xảy ra khi tăng số lượng");
         }
        	
    }
    private void openHoaDon(String tenHoaDon) {

        File file = new File("");
        File URL = new File("D:\\report\\" + tenHoaDon + ".pdf");
        try {
            Desktop.getDesktop().open(URL);
        } catch (Exception e) {

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lbl_maSanPham = new javax.swing.JLabel();
        txt_maSanPham = new javax.swing.JTextField();
        lbl_tenSanPham = new javax.swing.JLabel();
        txt_tenSanPham = new javax.swing.JTextField();
        lbl_loaiSanPham = new javax.swing.JLabel();
        lbl_mauSac = new javax.swing.JLabel();
        lbl_kichCo = new javax.swing.JLabel();
        cb_loaiSanPham = new javax.swing.JComboBox<>();
        cb_mauSac = new javax.swing.JComboBox<>();
        cb_kichCo = new javax.swing.JComboBox<>();
        pn_hinhAnh = new javax.swing.JPanel();
        lblHinhAnh = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_danhSachSanPham = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbl_chiTietHoaDon = new javax.swing.JTable();
        lbl_soLuong = new javax.swing.JLabel();
        txt_soLuong = new javax.swing.JTextField();
        btn_them = new javax.swing.JButton();
        btn_giam = new javax.swing.JButton();
        btn_tang = new javax.swing.JButton();
        btn_xoaTatCa = new javax.swing.JButton();
        btn_xoaSanPham = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        lbl_soDienThoai = new javax.swing.JLabel();
        txt_soDienThoai = new javax.swing.JTextField();
        btn_tim = new javax.swing.JButton();
        btn_themKhachHang = new javax.swing.JButton();
        lbl_tenKhachHang = new javax.swing.JLabel();
        txt_tenKhachHang = new javax.swing.JTextField();
        lbl_gioiTinh = new javax.swing.JLabel();
        txt_gioiTinh = new javax.swing.JTextField();
        lbl_gmail = new javax.swing.JLabel();
        txt_gmail = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
        lbl_thanhTienTitle = new javax.swing.JLabel();
        lbl_thanhTienValue = new javax.swing.JLabel();
        lbl_thueTitle = new javax.swing.JLabel();
        lbl_thueValue = new javax.swing.JLabel();
        lbl_tongTienTitle = new javax.swing.JLabel();
        lbl_tongTienValue = new javax.swing.JLabel();
        lbl_tienKhachDua = new javax.swing.JLabel();
        txt_tienKhachDua = new javax.swing.JTextField();
        lbl_tienThua = new javax.swing.JLabel();
        lbl_tienThuaValue = new javax.swing.JLabel();
        btn_luuTam = new javax.swing.JButton();
        btn_xuLyTam = new javax.swing.JButton();
        btn_Huy = new javax.swing.JButton();
        btn_thanhToan = new javax.swing.JButton();

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tìm sản phẩm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        lbl_maSanPham.setText("Mã sản phẩm");
        
        txt_maSanPham.setFont(new java.awt.Font("Calibri", 0, 12)); 
        txt_maSanPham.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_maSanPhamKeyReleased(evt);
            }
        });

        lbl_tenSanPham.setText("Tên sản phẩm");
        txt_tenSanPham.setEditable(false);
        txt_tenSanPham.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_tenSanPhamKeyReleased(null);
            }
        });

        lbl_loaiSanPham.setText("Loại sản phẩm");

        lbl_mauSac.setText("Màu sắc");

        lbl_kichCo.setText("Kích cỡ");

        cb_loaiSanPham.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả" }));
        cb_loaiSanPham.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cb_loaiSanPhamItemStateChanged(evt);
            }
        });
        cb_mauSac.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả" }));
        cb_mauSac.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cb_mauSacItemStateChanged(evt);
            }
        });
        cb_kichCo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả" }));
        cb_kichCo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cb_kichCoItemStateChanged(evt);
            }
        });
        lblHinhAnh.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblHinhAnh.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102), 3));
        lblHinhAnh.setMaximumSize(new java.awt.Dimension(159, 143));
        lblHinhAnh.setMinimumSize(new java.awt.Dimension(159, 143));
        lblHinhAnh.setPreferredSize(new java.awt.Dimension(159, 143));
        
        txt_tenSanPham.setFont(new java.awt.Font("Calibri", 0, 12)); 
        txt_tenSanPham.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_tenSanPhamKeyReleased(evt);
            }
        });
        
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Hình Ảnh");

        javax.swing.GroupLayout pn_hinhAnhLayout = new javax.swing.GroupLayout(pn_hinhAnh);
        pn_hinhAnh.setLayout(pn_hinhAnhLayout);
        pn_hinhAnhLayout.setHorizontalGroup(
            pn_hinhAnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pn_hinhAnhLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pn_hinhAnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHinhAnh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE))
                .addContainerGap())
        );
        pn_hinhAnhLayout.setVerticalGroup(
            pn_hinhAnhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pn_hinhAnhLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblHinhAnh, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_maSanPham)
                    .addComponent(lbl_tenSanPham)
                    .addComponent(lbl_loaiSanPham)
                    .addComponent(lbl_mauSac)
                    .addComponent(lbl_kichCo))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_tenSanPham)
                    .addComponent(cb_loaiSanPham, 0, 338, Short.MAX_VALUE)
                    .addComponent(cb_mauSac, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cb_kichCo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(txt_maSanPham))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pn_hinhAnh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_maSanPham)
                    .addComponent(txt_maSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_tenSanPham)
                    .addComponent(txt_tenSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_loaiSanPham)
                    .addComponent(cb_loaiSanPham, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_mauSac)
                    .addComponent(cb_mauSac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_kichCo)
                    .addComponent(cb_kichCo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(pn_hinhAnh, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Danh sách sản phẩm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        tbl_danhSachSanPham.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã sản phẩm", "Tên sản phẩm", "Loại sản phẩm", "Đơn giá", "Số lượng", "Màu sắc", "Chất liệu", "Kích thước"
            }
        ));
        tbl_danhSachSanPham.setRowHeight(30);
        tbl_danhSachSanPham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbl_danhSachSanPhamMousePressed(evt);
            }
        });
//        jScrollPane1.setViewportView(tbl_danhSachSanPham);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Hóa đơn thanh toán", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        tbl_chiTietHoaDon.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã sản phẩm", "Tên sản phẩm", "Loại sản phẩm", "Đơn giá", "Số lượng", "Màu sắc", "Chất liệu", "Kích thước"
            }
        ));
        tbl_chiTietHoaDon.setRowHeight(30);
        jScrollPane2.setViewportView(tbl_chiTietHoaDon);

        lbl_soLuong.setText("Số lượng");

        btn_them.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Approve.png"))); // NOI18N
        btn_them.setText("Thêm");
        btn_them.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_themMouseClicked(evt);
            }
        });

        btn_giam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Download.png"))); // NOI18N
        btn_giam.setText("Giảm");
        btn_giam.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_giamMouseClicked(evt);
            }
        });

        btn_tang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Arrow up.png"))); // NOI18N
        btn_tang.setText("Tăng");
        btn_tang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_tangMouseClicked(evt);
            }
        });

        btn_xoaTatCa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/delete-icon.png"))); // NOI18N
        btn_xoaTatCa.setText("Xóa tất cả");
        btn_xoaTatCa.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_xoaTatCaMouseClicked(evt);
            }
        });

        btn_xoaSanPham.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/delete.png"))); // NOI18N
        btn_xoaSanPham.setText("Xóa sản phẩm");
        btn_xoaSanPham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_xoaSanPhamMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(lbl_soLuong)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_soLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_them)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_giam)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_tang, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 83, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_xoaSanPham)
                            .addComponent(btn_xoaTatCa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_soLuong, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(txt_soLuong, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(btn_them, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_xoaSanPham, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                            .addComponent(btn_giam, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_tang, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_xoaTatCa)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông tin khách hàng", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        lbl_soDienThoai.setText("Số điện thọai");

        btn_tim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/search.png"))); // NOI18N
        btn_tim.setText("Tìm");
        btn_tim.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_timMouseClicked(evt);
            }
        });

        btn_themKhachHang.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Member Female.png"))); // NOI18N
        btn_themKhachHang.setText("Thêm khách hàng");
        btn_themKhachHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_themKhachHangMouseClicked(evt);
            }
        });

        lbl_tenKhachHang.setText("Tên khách hàng");
        txt_tenKhachHang.setEnabled(false);

        lbl_gioiTinh.setText("Giới tính");
        txt_gioiTinh.setEnabled(false);

        lbl_gmail.setText("Email");
        txt_gmail.setEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lbl_tenKhachHang)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_tenKhachHang))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lbl_gioiTinh)
                        .addGap(43, 43, 43)
                        .addComponent(txt_gioiTinh))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lbl_soDienThoai)
                        .addGap(18, 18, 18)
                        .addComponent(txt_soDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_tim)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btn_themKhachHang)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(lbl_gmail)
                        .addGap(58, 58, 58)
                        .addComponent(txt_gmail)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_soDienThoai)
                    .addComponent(txt_soDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_tim)
                    .addComponent(btn_themKhachHang))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_tenKhachHang)
                    .addComponent(txt_tenKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_gioiTinh)
                    .addComponent(txt_gioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_gmail)
                    .addComponent(txt_gmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông tin thanh toán", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12))); // NOI18N

        lbl_thanhTienTitle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_thanhTienTitle.setText("Thành tiền");

        lbl_thanhTienValue.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_thanhTienValue.setText("0");

        lbl_thueTitle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_thueTitle.setText("Thuế (VAT)");

        lbl_thueValue.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_thueValue.setText("0");

        lbl_tongTienTitle.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_tongTienTitle.setText("Tổng tiền");

        lbl_tongTienValue.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_tongTienValue.setText("0");

        lbl_tienKhachDua.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_tienKhachDua.setText("Tiền khách đưa");

        txt_tienKhachDua.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        txt_tienKhachDua.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txt_tienKhachDuaKeyReleased(evt);
            }
        });

        lbl_tienThua.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_tienThua.setText("Tiền thừa");

        lbl_tienThuaValue.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_tienThuaValue.setText("0");

        btn_luuTam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Download Icon.png"))); // NOI18N
        btn_luuTam.setText("Lưu tạm");

        btn_xuLyTam.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Topic.png"))); // NOI18N
        btn_xuLyTam.setText("Xử lý lưu tạm");

        btn_Huy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Discard.png"))); // NOI18N
        btn_Huy.setText("Hủy");

        btn_thanhToan.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/Shopping Cart (W.I.P.).png"))); // NOI18N
        btn_thanhToan.setText("Thanh toán");
        btn_thanhToan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_thanhToanMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_tienKhachDua, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_thanhTienValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_thueValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_tongTienValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_tienThuaValue, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_Huy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lbl_tienThua)
                                    .addComponent(lbl_thanhTienTitle)
                                    .addComponent(lbl_tongTienTitle)
                                    .addComponent(lbl_tienKhachDua)
                                    .addComponent(lbl_thueTitle))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(btn_luuTam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_thanhToan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_xuLyTam, javax.swing.GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(lbl_thanhTienTitle)
                .addGap(18, 18, 18)
                .addComponent(lbl_thanhTienValue)
                .addGap(18, 18, 18)
                .addComponent(lbl_thueTitle)
                .addGap(18, 18, 18)
                .addComponent(lbl_thueValue)
                .addGap(18, 18, 18)
                .addComponent(lbl_tongTienTitle)
                .addGap(18, 18, 18)
                .addComponent(lbl_tongTienValue)
                .addGap(18, 18, 18)
                .addComponent(lbl_tienKhachDua)
                .addGap(18, 18, 18)
                .addComponent(txt_tienKhachDua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lbl_tienThua)
                .addGap(18, 18, 18)
                .addComponent(lbl_tienThuaValue)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_xuLyTam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_luuTam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_thanhToan, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(btn_Huy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

private void btn_timMouseClicked(java.awt.event.MouseEvent evt) {
        
        if (!isValidInput()) {
            return;
        }

        btnTimHandler();
    }

    private void btn_themMouseClicked(java.awt.event.MouseEvent evt) {
        
        if (!isThemValid()) {
            return;
        }

        themCTHDHandler();
    }

    private void btn_xoaSanPhamMouseClicked(java.awt.event.MouseEvent evt) {
        
        xoaSanPhamHandler();
    }

    private void btn_thanhToanMouseClicked(java.awt.event.MouseEvent evt) {
        
        if (!isThanhToanHopLe()) {
            return;
        }

        thanhToanHandler();
        
    }

    private void btn_xoaTatCaMouseClicked(java.awt.event.MouseEvent evt) {
        
        xoaTatCaHandler();
    }

    private void cb_mauSacItemStateChanged(java.awt.event.ItemEvent evt) {
        
        tblDanhSachSanPhamWithFilter();
    }

    private void cb_loaiSanPhamItemStateChanged(java.awt.event.ItemEvent evt) {
        
        tblDanhSachSanPhamWithFilter();
    }

    private void cb_kichCoItemStateChanged(java.awt.event.ItemEvent evt) {
        
        tblDanhSachSanPhamWithFilter();
    }

    private void txt_maSanPhamKeyReleased(java.awt.event.KeyEvent evt) {

    	jScrollPane1.setViewportView(tbl_danhSachSanPham);

        tblDanhSachSanPhamWithFilter();
    }

    private void txt_tenSanPhamKeyReleased(java.awt.event.KeyEvent evt) {
        
        tblDanhSachSanPhamWithFilter();
    }


    private void txt_tienKhachDuaKeyReleased(java.awt.event.KeyEvent evt) {
        
        tienThuaHandler();
    }

    private void tblCTHD_changeHandler() {
        tbl_chiTietHoaDon.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (tbl_chiTietHoaDon.getRowCount() < 1) {
                    isThanhToan = false;
                } else {
                    isThanhToan = true;
                }
            }
        });
    }

    public ImageIcon ResizeImage(String imgPath) {
        ImageIcon myImage = new ImageIcon(imgPath);
        Image img = myImage.getImage();
        Image newImg = img.getScaledInstance(lblHinhAnh.getWidth(), lblHinhAnh.getHeight(), Image.SCALE_SMOOTH);
        ImageIcon image = new ImageIcon(newImg);

        return image;
    }

    private void tbl_danhSachSanPhamMousePressed(java.awt.event.MouseEvent evt) {
        
        String id = (String) tbl_danhSachSanPham.getValueAt(tbl_danhSachSanPham.getSelectedRow(), 0);
        sanPham = sanPham_DAO.getSanPhamById(id);
        int soLuong = Integer.parseInt(tbl_danhSachSanPham.getValueAt(tbl_danhSachSanPham.getSelectedRow(), 4).toString());
        sanPham.setSoLuong(soLuong);
        lblHinhAnh.setText("");
        File file = new File("");
        String path = file.getAbsolutePath();



        lblHinhAnh.setIcon(ResizeImage(path + "/data/picture/" + sanPham.getHinhAnh()));
    }

    private void btn_giamMouseClicked(java.awt.event.MouseEvent evt) {
        
        giamSoLuongHandler();
    }
    
    protected void btn_tangMouseClicked(MouseEvent evt) {
		tangSoLuongHandler();
		
	}

    private void btn_themKhachHangMouseClicked(java.awt.event.MouseEvent evt) {
        

        TrangChu.activeQLKHForm();
    }

    public void setEvent() {
        tbl_danhSachSanPham.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbl_danhSachSanPhamMousePressed(evt);
            }
        });

        tbl_chiTietHoaDon.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tbl_chiTietHoaDon.getSelectedRow() == -1) {
                    btn_giam.setEnabled(false);
                } else {
                    btn_giam.setEnabled(true);
                }
            }
        });

        tbl_chiTietHoaDon.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (tbl_chiTietHoaDon.getSelectedRow() == -1) {
                    btn_tang.setEnabled(false);
                } else {
                    btn_tang.setEnabled(true);
                }
            }
        });


    }
    
    




    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_giam;
    private javax.swing.JButton btn_tang;
    private javax.swing.JButton btn_them;
    private javax.swing.JButton btn_themKhachHang;
    private javax.swing.JButton btn_tim;
    private javax.swing.JButton btn_xoaSanPham;
    private javax.swing.JButton btn_xoaTatCa;
    private static javax.swing.JComboBox<String> cb_kichCo;
    private static javax.swing.JComboBox<String> cb_loaiSanPham;
    private static javax.swing.JComboBox<String> cb_mauSac;
    private javax.swing.JButton btn_luuTam;
    private javax.swing.JButton btn_xuLyTam;
    private javax.swing.JButton btn_Huy;
    private javax.swing.JButton btn_thanhToan;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel pn_hinhAnh;
    private javax.swing.JLabel lbl_gioiTinh;
    private javax.swing.JLabel lbl_gmail;
    private static javax.swing.JLabel lblHinhAnh;
    private javax.swing.JLabel lbl_kichCo;
    private javax.swing.JLabel lbl_loaiSanPham;
    private javax.swing.JLabel lbl_maSanPham;
    private javax.swing.JLabel lbl_mauSac;
    private javax.swing.JLabel lbl_soDienThoai;
    private javax.swing.JLabel lbl_soLuong;
    private javax.swing.JLabel lbl_tenKhachHang;
    private javax.swing.JLabel lbl_tenSanPham;
    private javax.swing.JLabel lbl_thanhTienTitle;
    private static javax.swing.JLabel lbl_thanhTienValue;
    private  javax.swing.JLabel lbl_thueTitle;
    private static javax.swing.JLabel lbl_thueValue;
    private javax.swing.JLabel lbl_tienKhachDua;
    private javax.swing.JLabel lbl_tienThua;
    private javax.swing.JLabel lbl_tienThuaValue;
    private javax.swing.JLabel lbl_tongTienTitle;
    private static javax.swing.JLabel lbl_tongTienValue;
    private javax.swing.JLabel jLabel1;
    private static javax.swing.JTable tbl_chiTietHoaDon;
    private static javax.swing.JTable tbl_danhSachSanPham;
    private static javax.swing.JTextField txt_gioiTinh;
    private static javax.swing.JTextField txt_gmail;
    private static javax.swing.JTextField txt_maSanPham;
    private static javax.swing.JTextField txt_soDienThoai;
    private static javax.swing.JTextField txt_soLuong;
    private static javax.swing.JTextField txt_tenKhachHang;
    private static javax.swing.JTextField txt_tenSanPham;
    private static javax.swing.JTextField txt_tienKhachDua;
    // End of variables declaration//GEN-END:variables
    
 // in hoá đơn ra file pdf

    public static void xuatHoaDon(HoaDon hd, ArrayList<ChiTietHoaDon> listCTHD, KhachHang kh, NhanVien nv) {
        try {
            String pathFull = "data/HoaDon/" + hd.getMaHoaDon() + ".pdf";
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(pathFull));
            document.open();
    
           
            Font font = FontFactory.getFont("src/Font/vuArial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Paragraph paragraph = new Paragraph("Hoá Đơn Bán Hàng", font);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));

            // Thông tin hóa đơn

            
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            float[] columnWidths = {1f, 1f};
            table.setWidths(columnWidths);

            PdfPCell cell1 = new PdfPCell(new Paragraph("Mã hóa đơn: " + hd.getMaHoaDon(), font));
            cell1.setBorderColor(BaseColor.WHITE);
            table.addCell(cell1);

            Date arr = hd.getNgayLap();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String ngayLap = formatter.format(arr);

            PdfPCell cell2 = new PdfPCell(new Paragraph("Ngày lập: " + ngayLap, font));
            cell2.setBorderColor(BaseColor.WHITE);
            table.addCell(cell2);

            PdfPCell cell3 = new PdfPCell(new Paragraph("Tên khách hàng: " + kh.getHoVaTen(), font));
            cell3.setBorderColor(BaseColor.WHITE);
            table.addCell(cell3);

            PdfPCell cell4 = new PdfPCell(new Paragraph("Số điện thoại: " + kh.getSdt(), font));
            cell4.setBorderColor(BaseColor.WHITE);
            table.addCell(cell4);

            PdfPCell cell55 = new PdfPCell(new Paragraph("Mã Nhân viên: " + nv.getMaNhanVien(), font));
            cell55.setBorderColor(BaseColor.WHITE);
            table.addCell(cell55);

            PdfPCell cell66 = new PdfPCell(new Paragraph("Nhân viên: " + nv.getHoVaTen(), font));
            cell66.setBorderColor(BaseColor.WHITE);
            table.addCell(cell66);


            document.add(table);

            // Thông tin chi tiết hóa đơn
            PdfPTable table1 = new PdfPTable(5);
            table1.setWidthPercentage(100);
            table1.setSpacingBefore(10f);
            table1.setSpacingAfter(10f);

            float[] columnWidths1 = {1f, 1f, 1f, 1f, 1f};
            table1.setWidths(columnWidths1);

            PdfPCell cell5 = new PdfPCell(new Paragraph("STT", font));
            cell5.setBorderColor(BaseColor.WHITE);
            table1.addCell(cell5);

            PdfPCell cell6 = new PdfPCell(new Paragraph("Tên sản phẩm", font));
            cell6.setBorderColor(BaseColor.WHITE);
            table1.addCell(cell6);

            PdfPCell cell7 = new PdfPCell(new Paragraph("Số lượng", font));
            cell7.setBorderColor(BaseColor.WHITE);
            table1.addCell(cell7);

            PdfPCell cell8 = new PdfPCell(new Paragraph("Đơn giá", font));
            cell8.setBorderColor(BaseColor.WHITE);
            table1.addCell(cell8);

            PdfPCell cell9 = new PdfPCell(new Paragraph("Thành tiền", font));
            cell9.setBorderColor(BaseColor.WHITE);
            table1.addCell(cell9);

            int i = 1;
            int tongTien = 0;
            
            int tienKhachTra = 0;

            for (ChiTietHoaDon cthd : listCTHD) {
                PdfPCell cell10 = new PdfPCell(new Paragraph(String.valueOf(i), font));
                cell10.setBorderColor(BaseColor.WHITE);
                table1.addCell(cell10);

                PdfPCell cell11 = new PdfPCell(new Paragraph(cthd.getSanPham().getTenSP(), font));
                cell11.setBorderColor(BaseColor.WHITE);
                table1.addCell(cell11);

                PdfPCell cell12 = new PdfPCell(new Paragraph(String.valueOf(cthd.getSoLuong()), font));
                cell12.setBorderColor(BaseColor.WHITE);
                table1.addCell(cell12);

                long giaBan = 0;
                if (cthd.getSanPham().getKhuyenMai().getSoLuong() > 0) {
                    giaBan = cthd.getSanPham().tinhGiaBanLucSau();
                } else {
                    giaBan = cthd.getSanPham().tinhGiaBanLucDau();
                }

                PdfPCell cell13 = new PdfPCell(new Paragraph(String.valueOf(giaBan), font));
                cell13.setBorderColor(BaseColor.WHITE);
                table1.addCell(cell13);

                //tính tổng tiền của sản phẩm
                long tongTienSP = giaBan * cthd.getSoLuong();
                
                
                

                PdfPCell cell14 = new PdfPCell(new Paragraph(String.valueOf(tongTienSP), font));
                cell14.setBorderColor(BaseColor.WHITE);
                table1.addCell(cell14);

                i++;
            }

            document.add(table1);

            // Thông tin khách hàng


            
            PdfPTable table2 = new PdfPTable(2);
            table2.setWidthPercentage(100);
            table2.setSpacingBefore(10f);
            table2.setSpacingAfter(10f);

            float[] columnWidths2 = {1f, 1f};
            table2.setWidths(columnWidths2);

  

            // Tính tổng tiền của hóa đơn
            long tongTienHD = 0;
            for (ChiTietHoaDon cthd : listCTHD) {
                long giaBan = 0;
                if (cthd.getSanPham().getKhuyenMai().getSoLuong() > 0) {
                    giaBan = cthd.getSanPham().tinhGiaBanLucSau();
                } else {
                    giaBan = cthd.getSanPham().tinhGiaBanLucDau();
                }
                
                long thanhTien = giaBan * cthd.getSoLuong();

                tongTienHD += thanhTien;
            }

            if (txt_tienKhachDua == null) {
                tienKhachTra = tongTien;
            } else {
                tienKhachTra = Integer.parseInt(txt_tienKhachDua.getText().trim());
            }

  

            long tienThue = (long) (tongTienHD * 0.015); // Tính tiền thuế
            long tongTienSauThue = tongTienHD + tienThue; // Tính tổng tiền sau thuế
//
            long tienThua = tienKhachTra - tongTienSauThue;
            
            PdfPTable tableVertical = new PdfPTable(1);
            tableVertical.setWidthPercentage(100);
            tableVertical.setSpacingBefore(10f);
            tableVertical.setSpacingAfter(10f);

            PdfPCell cellThanhTien = new PdfPCell(new Paragraph("Thành tiền:", font));
            cellThanhTien.setBorderColor(BaseColor.WHITE);
            tableVertical.addCell(cellThanhTien);

            PdfPCell cellThue = new PdfPCell(new Paragraph("Thuế(VAT):", font));
            cellThue.setBorderColor(BaseColor.WHITE);
            tableVertical.addCell(cellThue);

            PdfPCell cellTongTienSauThue = new PdfPCell(new Paragraph("Tổng tiền: ", font));
            cellTongTienSauThue.setBorderColor(BaseColor.WHITE);
            tableVertical.addCell(cellTongTienSauThue);

            PdfPCell cellTienKhachDua = new PdfPCell(new Paragraph("Khách đưa: ", font));
            cellTienKhachDua.setBorderColor(BaseColor.WHITE);
            tableVertical.addCell(cellTienKhachDua);

            PdfPCell cellTienThua = new PdfPCell(new Paragraph("Tiền thừa: ", font));
            cellTienThua.setBorderColor(BaseColor.WHITE);
            tableVertical.addCell(cellTienThua);

            // Tạo table dọc chứa các cột tiền
            PdfPTable tableMoney = new PdfPTable(1);
            tableMoney.setWidthPercentage(100);
            tableMoney.setSpacingBefore(10f);
            tableMoney.setSpacingAfter(10f);


            
            DecimalFormat decimalFormat = new DecimalFormat("#,###");

         // Định dạng các số cần hiển thị
         String formattedTongTienHD = decimalFormat.format(tongTienHD);
         String formattedTienThue = decimalFormat.format(tienThue);
         String formattedTongTienSauThue = decimalFormat.format(tongTienSauThue);
         String formattedTienKhachTra = decimalFormat.format(tienKhachTra);
         String formattedTienThua = decimalFormat.format(tienThua);

         // Tạo các ô chứa các giá trị đã được định dạng
         PdfPCell cellValueThanhTien = new PdfPCell(new Paragraph(formattedTongTienHD, font));
         cellValueThanhTien.setBorderColor(BaseColor.WHITE);
         cellValueThanhTien.setHorizontalAlignment(Element.ALIGN_RIGHT);
         tableMoney.addCell(cellValueThanhTien);

         PdfPCell cellValueThue = new PdfPCell(new Paragraph(formattedTienThue, font));
         cellValueThue.setBorderColor(BaseColor.WHITE);
         cellValueThue.setHorizontalAlignment(Element.ALIGN_RIGHT);
         tableMoney.addCell(cellValueThue);

         PdfPCell cellValueTongTienSauThue = new PdfPCell(new Paragraph(formattedTongTienSauThue, font));
         cellValueTongTienSauThue.setBorderColor(BaseColor.WHITE);
         cellValueTongTienSauThue.setHorizontalAlignment(Element.ALIGN_RIGHT);
         tableMoney.addCell(cellValueTongTienSauThue);

         PdfPCell cellValueTienKhachDua = new PdfPCell(new Paragraph(formattedTienKhachTra, font));
         cellValueTienKhachDua.setBorderColor(BaseColor.WHITE);
         cellValueTienKhachDua.setHorizontalAlignment(Element.ALIGN_RIGHT);
         tableMoney.addCell(cellValueTienKhachDua);

         PdfPCell cellValueTienThua = new PdfPCell(new Paragraph(formattedTienThua, font));
         cellValueTienThua.setBorderColor(BaseColor.WHITE);
         cellValueTienThua.setHorizontalAlignment(Element.ALIGN_RIGHT);
         tableMoney.addCell(cellValueTienThua);

            // Tạo table lớn chứa cả hai table nhỏ
            PdfPTable tableMain = new PdfPTable(2);
            tableMain.setWidthPercentage(100);
            tableMain.setSpacingBefore(10f);
            tableMain.setSpacingAfter(10f);

            PdfPCell cellInfo = new PdfPCell(tableVertical);
            cellInfo.setBorderColor(BaseColor.WHITE);
            tableMain.addCell(cellInfo);

            PdfPCell cellMoney = new PdfPCell(tableMoney);
            cellMoney.setBorderColor(BaseColor.WHITE);
            tableMain.addCell(cellMoney);

            document.add(tableMain);

            //tạo vùng chữ ký ở cuối hóa đơn
            Paragraph paragraph1 = new Paragraph();
            paragraph1.add(new Chunk(new VerticalPositionMark()));
            document.add(paragraph1);
            
            //tạo chữ ký
            Paragraph paragraph2 = new Paragraph();
            paragraph2.add(new Chunk("Người lập hóa đơn", font));
            paragraph2.add(new Chunk(new VerticalPositionMark()));
            paragraph2.add(new Chunk("Khách hàng", font));
            document.add(paragraph2);


            document.close();

            // mở file pdf
            try {
                File file = new File(pathFull);
                if (file.exists()) {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(file);
                    } else {
                        System.out.println("Awt Desktop is not supported!");
                    }
                } else {
                    System.out.println("File is not exists!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
        }
    }
}
