package pk.shoplus.service;

import java.util.ArrayList;
import java.util.List;

import pk.shoplus.model.Role;
import pk.shoplus.model.User;
import pk.shoplus.parameter.Permission;
import spark.Request;

public class SessionService {

	/**
	 * Store user into session
	 *
	 * @param req
	 * @param user
	 */
	public static void sessionVendorLogin(Request req, User user) {
		req.session().attribute("sessionUser", user);
		req.session().attribute("isVendor", true);

		List<String> menus = new ArrayList<String>();
		List<Permission> vendorPermissions = new ArrayList<>();
		vendorPermissions.add(Permission.VendorDashboard);
		vendorPermissions.add(Permission.VendorInformation);
		vendorPermissions.add(Permission.VendorProduct);
		for (Permission p : vendorPermissions) {
			menus.add(p.getName());
		}
		req.session().attribute("sessionMenus", menus);
	}

	/**
	 * Store user into session
	 *
	 * @param req
	 * @param user
	 */
	public static void sessionAdminLogin(Request req, User user, Role role) {
		req.session().attribute("sessionUser", user);
		req.session().attribute("isAdmin", true);
		if (role != null) {
			req.session().attribute("sessionRole", role);
			List<Permission> permissions = Permission.getPermissions(role.privilege);
			List<String> menus = new ArrayList<String>();
			// if set all permission
			if (Permission.hasPermission(role.privilege, Permission.All)) {
				List<Permission> all = Permission.getAllPermissions();
				for (Permission p : all) {
					menus.add(p.getName());
				}
				req.session().attribute("sessionMenus", menus);
			} else {
				for (Permission p : permissions) {
					menus.add(p.getName());
				}
			}

			req.session().attribute("sessionMenus", menus);
		}
	}
}
