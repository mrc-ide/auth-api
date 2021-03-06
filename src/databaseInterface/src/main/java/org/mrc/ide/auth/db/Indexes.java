/*
 * This file is generated by jOOQ.
*/
package org.mrc.ide.auth.db;


import javax.annotation.Generated;

import org.jooq.Index;
import org.jooq.OrderField;
import org.jooq.impl.Internal;
import org.mrc.ide.auth.db.tables.AppUser;
import org.mrc.ide.auth.db.tables.OnetimeToken;
import org.mrc.ide.auth.db.tables.Permission;
import org.mrc.ide.auth.db.tables.Role;
import org.mrc.ide.auth.db.tables.RolePermission;
import org.mrc.ide.auth.db.tables.UserGroup;
import org.mrc.ide.auth.db.tables.UserGroupMembership;
import org.mrc.ide.auth.db.tables.UserGroupRole;


/**
 * A class modelling indexes of tables of the <code>public</code> schema.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Indexes {

    // -------------------------------------------------------------------------
    // INDEX definitions
    // -------------------------------------------------------------------------

    public static final Index APP_USER_PKEY = Indexes0.APP_USER_PKEY;
    public static final Index ONETIME_TOKEN_PKEY = Indexes0.ONETIME_TOKEN_PKEY;
    public static final Index PERMISSION_PKEY = Indexes0.PERMISSION_PKEY;
    public static final Index ROLE_PKEY = Indexes0.ROLE_PKEY;
    public static final Index ROLE_PERMISSION_PKEY = Indexes0.ROLE_PERMISSION_PKEY;
    public static final Index USER_GROUP_PKEY = Indexes0.USER_GROUP_PKEY;
    public static final Index USER_GROUP_MEMBERSHIP_PKEY = Indexes0.USER_GROUP_MEMBERSHIP_PKEY;
    public static final Index USER_GROUP_ROLE_PKEY = Indexes0.USER_GROUP_ROLE_PKEY;

    // -------------------------------------------------------------------------
    // [#1459] distribute members to avoid static initialisers > 64kb
    // -------------------------------------------------------------------------

    private static class Indexes0 {
        public static Index APP_USER_PKEY = Internal.createIndex("app_user_pkey", AppUser.APP_USER, new OrderField[] { AppUser.APP_USER.USERNAME }, true);
        public static Index ONETIME_TOKEN_PKEY = Internal.createIndex("onetime_token_pkey", OnetimeToken.ONETIME_TOKEN, new OrderField[] { OnetimeToken.ONETIME_TOKEN.TOKEN }, true);
        public static Index PERMISSION_PKEY = Internal.createIndex("permission_pkey", Permission.PERMISSION, new OrderField[] { Permission.PERMISSION.NAME }, true);
        public static Index ROLE_PKEY = Internal.createIndex("role_pkey", Role.ROLE, new OrderField[] { Role.ROLE.ID }, true);
        public static Index ROLE_PERMISSION_PKEY = Internal.createIndex("role_permission_pkey", RolePermission.ROLE_PERMISSION, new OrderField[] { RolePermission.ROLE_PERMISSION.ROLE, RolePermission.ROLE_PERMISSION.PERMISSION }, true);
        public static Index USER_GROUP_PKEY = Internal.createIndex("user_group_pkey", UserGroup.USER_GROUP, new OrderField[] { UserGroup.USER_GROUP.ID }, true);
        public static Index USER_GROUP_MEMBERSHIP_PKEY = Internal.createIndex("user_group_membership_pkey", UserGroupMembership.USER_GROUP_MEMBERSHIP, new OrderField[] { UserGroupMembership.USER_GROUP_MEMBERSHIP.USERNAME, UserGroupMembership.USER_GROUP_MEMBERSHIP.USER_GROUP }, true);
        public static Index USER_GROUP_ROLE_PKEY = Internal.createIndex("user_group_role_pkey", UserGroupRole.USER_GROUP_ROLE, new OrderField[] { UserGroupRole.USER_GROUP_ROLE.USER_GROUP, UserGroupRole.USER_GROUP_ROLE.ROLE, UserGroupRole.USER_GROUP_ROLE.SCOPE_ID }, true);
    }
}
