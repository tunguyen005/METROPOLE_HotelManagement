package admin_dashboard.form;

import admin_dashboard.component.Header;
import admin_dashboard.component.Menu;
import admin_dashboard.event.EventMenuSelected;
import admin_dashboard.event.EventShowPopupMenu;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.TimingTargetAdapter;
import admin_dashboard.swing.MenuItem;
import admin_dashboard.swing.PopupMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScrDashboard extends JFrame {

    private MigLayout layout;
    private Menu menu;
    private Header header;
    private JPanel contentPanel;
    private Animator animator;

    public ScrDashboard() {
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);

        layout = new MigLayout("fill", "0[]0[100%, fill]0", "0[fill, top]0");
        setLayout(layout);

        menu = new Menu();
        header = new Header();
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        menu.addEvent(new EventMenuSelected() {
            @Override
            public void menuSelected(int menuIndex, int subMenuIndex) {
                System.out.println("Menu Index : " + menuIndex + " SubMenu Index " + subMenuIndex);
                try {
                    switch (menuIndex) {
                        case 0: // BÁO CÁO THỐNG KÊ
                            showForm(new ScrBaoCaoThongKe());
                            break;
                        case 1: // QUẢN LÝ PHÒNG
                            if (subMenuIndex == 0) showForm(new ScrQLPhong());
                            else if (subMenuIndex == 1) showForm(new ScrQLBooking());
                            break;
                        case 2: // QUẢN LÝ KHÁCH HÀNG
                            if (subMenuIndex == 0) showForm(new ScrQLKhachHang());
                            else if (subMenuIndex == 1) showForm(new ScrQLDichVu());
                            break;
                        case 3: // CHECK IN
                            showForm(new ScrCheckIn());
                            break;
                        case 4: // QUẢN LÝ NHÂN VIÊN
                            if (subMenuIndex == 0) showForm(new ScrQLNhanVien());
                            else if (subMenuIndex == 1) showForm(new ScrQLTaiXe());
                            else if (subMenuIndex == 2) showForm(new ScrQL());
                            break;
                        case 5: // CHECK OUT
                            showForm(new ScrCheckOut());
                            break;
                        case 6:
                            showForm(new ScrQLThanhToan());
                            break;
                        case 7: // ĐĂNG XUẤT
                            System.exit(0);
                            break;
                        default:
                            showForm(new ScrHome());
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(ScrDashboard.this, "Lỗi khi hiển thị form: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        menu.addEventShowPopup(new EventShowPopupMenu() {
            @Override
            public void showPopup(Component com) {
                MenuItem item = (MenuItem) com;
                PopupMenu popup = new PopupMenu(ScrDashboard.this, item.getIndex(), item.getEventSelected(), item.getMenu().getSubMenu());
                int x = ScrDashboard.this.getX() + 52;
                int y = ScrDashboard.this.getY() + com.getY() + 86;
                popup.setLocation(x, y);
                popup.setVisible(true);
            }
        });

        menu.initMenuItem();
        add(menu, "w 230!, spany 2");
        add(header, "h 50!, wrap");
        add(contentPanel, "w 100%, h 100%");

        TimingTarget target = new TimingTargetAdapter() {
            @Override
            public void timingEvent(float fraction) {
                double width;
                if (menu.isShowMenu()) {
                    width = 60 + (170 * (1f - fraction));
                } else {
                    width = 60 + (170 * fraction);
                }
                layout.setComponentConstraints(menu, "w " + width + "!, spany2");
                menu.revalidate();
                contentPanel.revalidate();
                revalidate();
                repaint();
            }

            @Override
            public void end() {
                menu.setShowMenu(!menu.isShowMenu());
                menu.setEnableMenu(true);
            }
        };

        animator = new Animator(500, target);
        animator.setResolution(0);
        animator.setDeceleration(0.5f);
        animator.setAcceleration(0.5f);
        header.addMenuEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                if (!animator.isRunning()) {
                    animator.start();
                }
                menu.setEnableMenu(false);
                if (menu.isShowMenu()) {
                    menu.hideallMenu();
                }
            }
        });

        // Khởi động với Form_Home
        showForm(new ScrHome());

        // Đặt kích thước lớn hơn và đảm bảo áp dụng
        setPreferredSize(new Dimension(1400, 800)); // Kích thước mong muốn
        setMinimumSize(new Dimension(800, 600));    // Kích thước tối thiểu
        pack();                                      // Tính toán layout
        setSize(1400, 800);                         // Ghi đè kích thước sau pack()

        setLocationRelativeTo(null);
    }

    public void showForm(Component form) {
        contentPanel.removeAll();
        contentPanel.add(form, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
        revalidate();
        repaint();
    }

    public static void main(String args[]) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(ScrDashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        EventQueue.invokeLater(() -> new ScrDashboard().setVisible(true));
    }
}