package pk.shoplus.parameter;

import java.util.ArrayList;
import java.util.List;

public enum Permission {

    /**
     * Index permission
     */
    Index("Index", 0),

    /**
     * All permission
     */
    All("All", 1),

    /**
     * Dashboard permission
     */
    AdminDashboard("AdminDashboard", 2),

    /**
     * User permission
     */
    AdminUser("AdminUser", 4),

    /**
     * Shop permission
     */
    AdminShop("AdminShop", 8),

    /**
     * Product permission
     */
    AdminProduct("AdminProduct", 16),

    /**
     * Order permission
     *
     */
    AdminOrder("AdminOrder", 32),

    /**
     * Promotion permission
     *
     */
    AdminPromotion("AdminPromotion", 64),

    /**
     * Vendor permission
     *
     */
    AdminVendor("AdminVendor", 128),

    /**
     * Brand permission
     *
     */
    AdminBrand("AdminBrand", 256),

    /**
     *
     */
    VendorDashboard("VendorDashboard", 512),

    /**
     *
     */
    VendorProduct("VendorProduct", 1024),

    /**
     *
     */
    VendorInformation("VendorInformation", 2048),

    /**
     *
     */
    AdminNotification("AdminNotification", 4096);

    /**
     * Inner static permission description class
     */
    public static class PermissionDetail {
        String name;
        long code;

        public PermissionDetail(String name, long code) {
            this.name = name;
            this.code = code;
        }

        public PermissionDetail() {
            this.name = "Index";
            this.code = 0;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

    /**
     * Permission value
     */
    private PermissionDetail permission;

    private Permission(String name, long i) {
        this.permission = new PermissionDetail();
        this.permission.name = name;
        this.permission.code = i;
    }

    /**
     * Get permission by name & code
     *
     * @param name
     * @param i
     * @return
     */
    public static Permission getPermission(String name, int i) {
        for (Permission p : Permission.values()) {
            if (p.getValue() == i && p.getName().equals(name)) {
                return p;
            }
        }

        return Permission.Index;
    }

    /**
     * Get permission by code
     *
     * @param i
     * @return
     */
    public static Permission getPermission(int i) {
        for (Permission p : Permission.values()) {
            if (p.getValue() == i) {
                return p;
            }
        }

        return Permission.Index;
    }

    /**
     * Get permission by name
     *
     * @param name
     * @return
     */
    public static Permission getPermission(String name) {
        for (Permission p : Permission.values()) {
            if (p.name().equals(name)) {
                return p;
            }
        }

        return Permission.Index;
    }

    /**
     * Parser permission from long value
     *
     * @param i
     * @return
     */
    public static List<Permission> getPermissions(long i) {
        List<Permission> ps = new ArrayList<Permission>();
        for (Permission p : Permission.values()) {
            if (hasPermission(i, p)) {
                ps.add(p);
            }
        }

        return ps;
    }


    public long getValue() {
        return this.permission.code;
    }

    public String getName() {
        return this.permission.name;
    }

    public PermissionDetail getPermissionType() {
        return this.permission;
    }

    /**
     *
     * @param privilege
     * @param permission
     * @return
     */
    public static boolean hasPermission(long privilege, Permission permission) {
        return (privilege & permission.getValue()) == permission.getValue();
    }

    /**
     *
     * @param privilege
     * @param permission
     * @return
     */
    public static long addPermission(long privilege, Permission permission) {
        return privilege | permission.getValue();
    }

    /**
     *
     * @return
     */
    public static List<Permission> getAllPermissions() {
        List<Permission> list = new ArrayList<Permission>();
        for (Permission p : Permission.values()) {
            list.add(p);
        }

        return list;
    }

    /**
     *
     * @return
     */
    public static List<PermissionDetail> getAllPermissionTypes() {
        List<PermissionDetail> list = new ArrayList<PermissionDetail>();
        for (Permission p : Permission.values()) {
            list.add(p.getPermissionType());
        }

        return list;
    }
}


