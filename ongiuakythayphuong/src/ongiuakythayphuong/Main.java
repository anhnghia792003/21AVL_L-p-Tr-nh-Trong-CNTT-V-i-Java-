package ongiuakythayphuong; 
import java.io.*;
import java.text.Collator;
import java.util.*;

public class Main {

    // ================= CUSTOMER =================
    static class Customer {
        private int id;
        private String name;
        private String gender; // "Nam" hoac "Nu"
        private int age;
        private String blood;  // A, B, O, AB

        public Customer(int id, String name, String gender, int age, String blood) {
            this.id = id;
            this.name = name;
            this.gender = gender;
            this.age = age;
            this.blood = blood;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getGender() { return gender; }
        public int getAge() { return age; }
        public String getBlood() { return blood; }

        public boolean isMale() { return gender.equalsIgnoreCase("Nam"); }

        /** Ten goi (tu cuoi cua ho va ten) */
        public String getFirstName() {
            String[] p = name.trim().split("\\s+");
            return p[p.length - 1];
        }

        /** Dinh dang ghi file: id|name|gender|age|blood */
        public String toFileLine() {
            return id + "|" + name + "|" + gender + "|" + age + "|" + blood;
        }

        public static Customer fromFileLine(String line) {
            String[] p = line.split("\\|");
            return new Customer(Integer.parseInt(p[0].trim()), p[1].trim(), p[2].trim(),
                    Integer.parseInt(p[3].trim()), p[4].trim());
        }
    }

    // ================= BST =================
    static class BST {
        private class Node {
            Customer data;
            Node left, right;
            Node(Customer c) { data = c; }
        }

        private Node root;
        private int size;

        public int size() { return size; }
        public boolean isEmpty() { return root == null; }

        // ---------- 1. Them (nhan node = customer ID) ----------
        public boolean insert(Customer c) {
            if (root == null) { root = new Node(c); size++; return true; }
            Node cur = root;
            while (true) {
                if (c.getId() == cur.data.getId()) return false; // trung ID
                if (c.getId() < cur.data.getId()) {
                    if (cur.left == null) { cur.left = new Node(c); size++; return true; }
                    cur = cur.left;
                } else {
                    if (cur.right == null) { cur.right = new Node(c); size++; return true; }
                    cur = cur.right;
                }
            }
        }

        // ---------- 7. Tim kiem theo ID ----------
        public Customer search(int id) {
            Node cur = root;
            while (cur != null) {
                if (id == cur.data.getId()) return cur.data;
                cur = (id < cur.data.getId()) ? cur.left : cur.right;
            }
            return null;
        }

        // ---------- 8. Xoa theo ID ----------
        public boolean delete(int id) {
            if (search(id) == null) return false;
            root = delete(root, id);
            size--;
            return true;
        }

        private Node delete(Node node, int id) {
            if (node == null) return null;
            if (id < node.data.getId()) node.left = delete(node.left, id);
            else if (id > node.data.getId()) node.right = delete(node.right, id);
            else {
                if (node.left == null) return node.right;
                if (node.right == null) return node.left;
                // 2 con: thay bang node nho nhat cua cay con phai
                Node min = node.right;
                while (min.left != null) min = min.left;
                node.data = min.data;
                node.right = delete(node.right, min.data.getId());
            }
            return node;
        }

        // ---------- Duyet cay ----------
        /** LNR: danh sach tang dan theo ID */
        public List<Customer> toList() {
            List<Customer> list = new ArrayList<>();
            inorder(root, list);
            return list;
        }

        private void inorder(Node n, List<Customer> list) {
            if (n == null) return;
            inorder(n.left, list);
            list.add(n.data);
            inorder(n.right, list);
        }

        /** NLR: dung khi luu file de doc lai se dung lai dung hinh dang cay */
        public List<Customer> toPreorderList() {
            List<Customer> list = new ArrayList<>();
            preorder(root, list);
            return list;
        }

        private void preorder(Node n, List<Customer> list) {
            if (n == null) return;
            list.add(n.data);
            preorder(n.left, list);
            preorder(n.right, list);
        }

        // ---------- 3. Thong ke tuoi ----------
        public double averageAge() {
            if (size == 0) return 0;
            long sum = 0;
            for (Customer c : toList()) sum += c.getAge();
            return (double) sum / size;
        }

        /** Lon tuoi nhat; gender = null => tat ca, "Nam"/"Nu" => loc theo gioi */
        public Customer oldest(String gender) {
            Customer best = null;
            for (Customer c : toList()) {
                if (gender != null && !c.getGender().equalsIgnoreCase(gender)) continue;
                if (best == null || c.getAge() > best.getAge()) best = c;
            }
            return best;
        }

        public Customer youngest(String gender) {
            Customer best = null;
            for (Customer c : toList()) {
                if (gender != null && !c.getGender().equalsIgnoreCase(gender)) continue;
                if (best == null || c.getAge() < best.getAge()) best = c;
            }
            return best;
        }

        // ---------- 4. Loc ----------
        public List<Customer> filterByGender(String gender) {
            List<Customer> res = new ArrayList<>();
            for (Customer c : toList())
                if (c.getGender().equalsIgnoreCase(gender)) res.add(c);
            return res;
        }

        public List<Customer> filterByBlood(String blood) {
            List<Customer> res = new ArrayList<>();
            for (Customer c : toList())
                if (c.getBlood().equalsIgnoreCase(blood)) res.add(c);
            return res;
        }
    }

    // ================= CHUONG TRINH CHINH =================
    static final String FILE_NAME = "customers.txt";
    static BST tree = new BST();
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadFromFile();   // 9. doc file moi khi chay chuong trinh
        int choice;
        do {
            menu();
            choice = readInt("Chon chuc nang: ");
            switch (choice) {
                case 1: inputCustomers(); break;
                case 2: printTable(tree.toList(), "DANH SACH KHACH HANG"); break;
                case 3: statistics(); break;
                case 4: listByGroup(); break;
                case 5: sortMaleByName(); break;
                case 6: maleOldestYoungest(); break;
                case 7: searchById(); break;
                case 8: deleteById(); break;
                case 9: saveToFile(); break;
                case 0: saveToFile(); System.out.println("Tam biet!"); break;
                default: System.out.println("Lua chon khong hop le!");
            }
        } while (choice != 0);
    }

    static void menu() {
        System.out.println("\n========== QUAN LY KHACH HANG (BST) ==========");
        System.out.println("1. Nhap danh sach khach hang");
        System.out.println("2. Xuat danh sach (dang bang)");
        System.out.println("3. Tuoi trung binh, lon nhat, nho nhat");
        System.out.println("4. DS nam / nu / theo nhom mau (A, B, O)");
        System.out.println("5. Sap xep theo ten va xuat DS khach hang nam");
        System.out.println("6. Khach hang nam lon tuoi nhat / nho tuoi nhat");
        System.out.println("7. Tim kiem theo ID");
        System.out.println("8. Xoa theo ID");
        System.out.println("9. Luu ra file");
        System.out.println("0. Thoat (tu dong luu)");
    }

    // ---------- tien ich nhap ----------
    static int readInt(String msg) {
        while (true) {
            System.out.print(msg);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Vui long nhap so nguyen!"); }
        }
    }

    static String readGender() {
        while (true) {
            System.out.print("Gioi tinh (Nam/Nu): ");
            String g = sc.nextLine().trim();
            if (g.equalsIgnoreCase("Nam")) return "Nam";
            if (g.equalsIgnoreCase("Nu") || g.equalsIgnoreCase("Nữ")) return "Nu";
            System.out.println("Chi nhan Nam hoac Nu!");
        }
    }

    static String readBlood() {
        while (true) {
            System.out.print("Nhom mau (A/B/O/AB): ");
            String b = sc.nextLine().trim().toUpperCase();
            if (b.equals("A") || b.equals("B") || b.equals("O") || b.equals("AB")) return b;
            System.out.println("Nhom mau khong hop le!");
        }
    }

    // ---------- 1. Nhap ----------
    static void inputCustomers() {
        int n = readInt("Nhap so luong khach hang: ");
        for (int i = 1; i <= n; i++) {
            System.out.println("--- Khach hang " + i + " ---");
            int id = readInt("ID: ");
            if (tree.search(id) != null) {
                System.out.println("ID da ton tai, bo qua!");
                continue;
            }
            System.out.print("Ho va ten: ");
            String name = sc.nextLine().trim();
            String gender = readGender();
            int age = readInt("Tuoi: ");
            String blood = readBlood();
            tree.insert(new Customer(id, name, gender, age, blood));
        }
    }

    // ---------- 2. Xuat bang ----------
    static void printTable(List<Customer> list, String title) {
        System.out.println("\n" + title);
        String line = "+--------+------------------------------+--------+-------+--------+";
        System.out.println(line);
        System.out.printf("| %-6s | %-28s | %-6s | %-5s | %-6s |%n", "ID", "Ho va ten", "Gioi", "Tuoi", "Mau");
        System.out.println(line);
        if (list.isEmpty()) System.out.printf("| %-64s |%n", "(Khong co du lieu)");
        for (Customer c : list)
            System.out.printf("| %-6d | %-28s | %-6s | %-5d | %-6s |%n",
                    c.getId(), c.getName(), c.getGender(), c.getAge(), c.getBlood());
        System.out.println(line);
    }

    // ---------- 3 ----------
    static void statistics() {
        if (tree.isEmpty()) { System.out.println("Danh sach rong!"); return; }
        System.out.printf("Tuoi trung binh: %.2f%n", tree.averageAge());
        printTable(Collections.singletonList(tree.oldest(null)), "KHACH HANG LON TUOI NHAT");
        printTable(Collections.singletonList(tree.youngest(null)), "KHACH HANG NHO TUOI NHAT");
    }

    // ---------- 4 ----------
    static void listByGroup() {
        printTable(tree.filterByGender("Nam"), "KHACH HANG NAM");
        printTable(tree.filterByGender("Nu"), "KHACH HANG NU");
        for (String b : new String[] { "A", "B", "O" })
            printTable(tree.filterByBlood(b), "KHACH HANG NHOM MAU " + b);
    }

    // ---------- 5 ----------
    static void sortMaleByName() {
        List<Customer> males = tree.filterByGender("Nam");
        final Collator col = Collator.getInstance(new Locale("vi", "VN"));
        // Sap theo ten goi (tu cuoi), neu trung thi theo ho va ten day du
        males.sort((a, b) -> {
            int r = col.compare(a.getFirstName(), b.getFirstName());
            return r != 0 ? r : col.compare(a.getName(), b.getName());
        });
        printTable(males, "KHACH HANG NAM (SAP XEP THEO TEN)");
    }

    // ---------- 6 ----------
    static void maleOldestYoungest() {
        Customer o = tree.oldest("Nam"), y = tree.youngest("Nam");
        if (o == null) { System.out.println("Khong co khach hang nam!"); return; }
        printTable(Collections.singletonList(o), "KHACH HANG NAM LON TUOI NHAT");
        printTable(Collections.singletonList(y), "KHACH HANG NAM NHO TUOI NHAT");
    }

    // ---------- 7 ----------
    static void searchById() {
        int id = readInt("Nhap ID can tim: ");
        Customer c = tree.search(id);
        if (c == null) System.out.println("Khong tim thay khach hang co ID = " + id);
        else printTable(Collections.singletonList(c), "KET QUA TIM KIEM");
    }

    // ---------- 8 ----------
    static void deleteById() {
        int id = readInt("Nhap ID can xoa: ");
        System.out.println(tree.delete(id) ? "Da xoa khach hang ID = " + id
                                           : "Khong tim thay ID = " + id);
    }

    // ---------- 9. File ----------
    static void saveToFile() {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(FILE_NAME), "UTF-8"))) {
            for (Customer c : tree.toPreorderList()) pw.println(c.toFileLine());
            System.out.println("Da luu " + tree.size() + " khach hang vao " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Loi ghi file: " + e.getMessage());
        }
    }

    static void loadFromFile() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(f), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                tree.insert(Customer.fromFileLine(line));
            }
            System.out.println("Da doc " + tree.size() + " khach hang tu " + FILE_NAME);
        } catch (Exception e) {
            System.out.println("Loi doc file: " + e.getMessage());
        }
    }
}
