package org.mrc.ide.auth.emails

import org.mrc.ide.auth.models.BasicUserProperties

interface EmailManager
{
    fun sendEmail(data: EmailData, recipient: BasicUserProperties)
}
