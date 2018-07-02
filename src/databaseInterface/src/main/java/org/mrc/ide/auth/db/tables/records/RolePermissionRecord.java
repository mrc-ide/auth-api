/*
 * This file is generated by jOOQ.
*/
package org.mrc.ide.auth.db.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.UpdatableRecordImpl;
import org.mrc.ide.auth.db.tables.RolePermission;


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
public class RolePermissionRecord extends UpdatableRecordImpl<RolePermissionRecord> implements Record2<Integer, String> {

    private static final long serialVersionUID = -1811021200;

    /**
     * Setter for <code>public.role_permission.role</code>.
     */
    public void setRole(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.role_permission.role</code>.
     */
    public Integer getRole() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.role_permission.permission</code>.
     */
    public void setPermission(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.role_permission.permission</code>.
     */
    public String getPermission() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record2<Integer, String> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<Integer, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<Integer, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return RolePermission.ROLE_PERMISSION.ROLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return RolePermission.ROLE_PERMISSION.PERMISSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getRole();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getPermission();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getRole();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getPermission();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RolePermissionRecord value1(Integer value) {
        setRole(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RolePermissionRecord value2(String value) {
        setPermission(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RolePermissionRecord values(Integer value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RolePermissionRecord
     */
    public RolePermissionRecord() {
        super(RolePermission.ROLE_PERMISSION);
    }

    /**
     * Create a detached, initialised RolePermissionRecord
     */
    public RolePermissionRecord(Integer role, String permission) {
        super(RolePermission.ROLE_PERMISSION);

        set(0, role);
        set(1, permission);
    }
}
