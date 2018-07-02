package org.mrc.ide.auth.db

import org.jooq.*
import org.jooq.impl.TableImpl
import org.postgresql.copy.CopyManager
import java.io.InputStream

// Just a helper so we can write `fetchInto<T>` instead of `fetchInto(T::class.java)`
inline fun <reified TRecord : Record> Select<*>.fetchInto(): List<TRecord> = this.fetchInto(TRecord::class.java)

// This helper avoids overloading ambiguity when the field type is "Any"
fun <T> TableField<*, T>.eqField(otherField: Field<T>): Condition = this.eq(otherField)

fun TableImpl<*>.fieldsAsList() = this.fields().toList()
