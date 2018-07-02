package org.mrc.ide.auth.models.helpers

@Target(AnnotationTarget.PROPERTY)
annotation class SerializationRule(val rule: Rule)

enum class Rule
{
    EXCLUDE_IF_NULL
}
