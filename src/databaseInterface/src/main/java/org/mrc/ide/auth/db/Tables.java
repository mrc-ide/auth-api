/*
 * This file is generated by jOOQ.
*/
package org.mrc.ide.auth.db;


import javax.annotation.Generated;

import org.mrc.ide.auth.db.tables.AppUser;
import org.mrc.ide.auth.db.tables.OnetimeToken;
import org.mrc.ide.auth.db.tables.Permission;
import org.mrc.ide.auth.db.tables.Role;
import org.mrc.ide.auth.db.tables.RolePermission;
import org.mrc.ide.auth.db.tables.UserGroup;
import org.mrc.ide.auth.db.tables.UserGroupMembership;
import org.mrc.ide.auth.db.tables.UserGroupRole;


/**
 * Convenience access to all tables in public
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>public.app_user</code>.
     */
    public static final AppUser APP_USER = org.mrc.ide.auth.db.tables.AppUser.APP_USER;

    /**
     * The table <code>public.onetime_token</code>.
     */
    public static final OnetimeToken ONETIME_TOKEN = org.mrc.ide.auth.db.tables.OnetimeToken.ONETIME_TOKEN;

    /**
     * The table <code>public.permission</code>.
     */
    public static final Permission PERMISSION = org.mrc.ide.auth.db.tables.Permission.PERMISSION;

    /**
     * The table <code>public.role</code>.
     */
    public static final Role ROLE = org.mrc.ide.auth.db.tables.Role.ROLE;

    /**
     * The table <code>public.role_permission</code>.
     */
    public static final RolePermission ROLE_PERMISSION = org.mrc.ide.auth.db.tables.RolePermission.ROLE_PERMISSION;

    /**
     * The table <code>public.user_group</code>.
     */
    public static final UserGroup USER_GROUP = org.mrc.ide.auth.db.tables.UserGroup.USER_GROUP;

    /**
     * The table <code>public.user_group_membership</code>.
     */
    public static final UserGroupMembership USER_GROUP_MEMBERSHIP = org.mrc.ide.auth.db.tables.UserGroupMembership.USER_GROUP_MEMBERSHIP;

    /**
     * The table <code>public.user_group_role</code>.
     */
    public static final UserGroupRole USER_GROUP_ROLE = org.mrc.ide.auth.db.tables.UserGroupRole.USER_GROUP_ROLE;
}
