package org.mrc.ide.auth.emails

import org.mrc.ide.auth.models.BasicUserProperties
import org.simplejavamail.email.Email
import org.simplejavamail.mailer.Mailer
import org.simplejavamail.mailer.config.ServerConfig
import org.simplejavamail.mailer.config.TransportStrategy
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class RealEmailManager : EmailManager
{
    private val logger: Logger = LoggerFactory.getLogger(RealEmailManager::class.java)

    override fun sendEmail(data: EmailData, recipient: BasicUserProperties)
    {
//        val mailer = Mailer(
//                ServerConfig(server, port, username, password),
//                TransportStrategy.SMTP_TLS
//        )
//        val email = Email().apply {
//            addNamedToRecipients(recipient.name, recipient.email)
//            setFromAddress("Montagu notifications", sender)
//            subject = data.subject
//            text = data.text()
//            textHTML = data.html()
//        }
//        mailer.sendMail(email)
        logger.info("mail sent to: ${recipient.email}")

    }
}