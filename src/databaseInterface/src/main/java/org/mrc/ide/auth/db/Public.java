/*
 * This file is generated by jOOQ.
*/
package org.mrc.ide.auth.db;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Sequence;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;
import org.mrc.ide.auth.db.tables.AppUser;
import org.mrc.ide.auth.db.tables.OnetimeToken;
import org.mrc.ide.auth.db.tables.Permission;
import org.mrc.ide.auth.db.tables.Role;
import org.mrc.ide.auth.db.tables.RolePermission;
import org.mrc.ide.auth.db.tables.UserGroup;
import org.mrc.ide.auth.db.tables.UserGroupMembership;
import org.mrc.ide.auth.db.tables.UserGroupRole;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Public extends SchemaImpl {

    private static final long serialVersionUID = 2085546278;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>public.app_user</code>.
     */
    public final AppUser APP_USER = org.mrc.ide.auth.db.tables.AppUser.APP_USER;

    /**
     * The table <code>public.onetime_token</code>.
     */
    public final OnetimeToken ONETIME_TOKEN = org.mrc.ide.auth.db.tables.OnetimeToken.ONETIME_TOKEN;

    /**
     * The table <code>public.permission</code>.
     */
    public final Permission PERMISSION = org.mrc.ide.auth.db.tables.Permission.PERMISSION;

    /**
     * The table <code>public.role</code>.
     */
    public final Role ROLE = org.mrc.ide.auth.db.tables.Role.ROLE;

    /**
     * The table <code>public.role_permission</code>.
     */
    public final RolePermission ROLE_PERMISSION = org.mrc.ide.auth.db.tables.RolePermission.ROLE_PERMISSION;

    /**
     * The table <code>public.user_group</code>.
     */
    public final UserGroup USER_GROUP = org.mrc.ide.auth.db.tables.UserGroup.USER_GROUP;

    /**
     * The table <code>public.user_group_membership</code>.
     */
    public final UserGroupMembership USER_GROUP_MEMBERSHIP = org.mrc.ide.auth.db.tables.UserGroupMembership.USER_GROUP_MEMBERSHIP;

    /**
     * The table <code>public.user_group_role</code>.
     */
    public final UserGroupRole USER_GROUP_ROLE = org.mrc.ide.auth.db.tables.UserGroupRole.USER_GROUP_ROLE;

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Sequence<?>> getSequences() {
        List result = new ArrayList();
        result.addAll(getSequences0());
        return result;
    }

    private final List<Sequence<?>> getSequences0() {
        return Arrays.<Sequence<?>>asList(
            Sequences.ROLE_ID_SEQ);
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            AppUser.APP_USER,
            OnetimeToken.ONETIME_TOKEN,
            Permission.PERMISSION,
            Role.ROLE,
            RolePermission.ROLE_PERMISSION,
            UserGroup.USER_GROUP,
            UserGroupMembership.USER_GROUP_MEMBERSHIP,
            UserGroupRole.USER_GROUP_ROLE);
    }
}
