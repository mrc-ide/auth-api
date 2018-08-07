package org.mrc.ide.auth.db

import org.jooq.*
import org.jooq.impl.TableImpl

// This helper avoids overloading ambiguity when the field type is "Any"
fun <T> TableField<*, T>.eqField(otherField: Field<T>): Condition = this.eq(otherField)

fun TableImpl<*>.fieldsAsList() = this.fields().toList()
