package org.mrc.ide.auth.db

import org.jooq.*
import org.jooq.impl.TableImpl

// Just a helper so we can write `org.mrc.ide.auth.db.fetchInto<T>` instead of `org.mrc.ide.auth.db.fetchInto(T::class.java)`
inline fun <reified TRecord : Record> Select<*>.fetchInto(): List<TRecord> = this.fetchInto(TRecord::class.java)

// This helper avoids overloading ambiguity when the field type is "Any"
fun <T> TableField<*, T>.eqField(otherField: Field<T>): Condition = this.eq(otherField)

fun TableImpl<*>.fieldsAsList() = this.fields().toList()
