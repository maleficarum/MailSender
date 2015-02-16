package mx.maleficarum

@Grapes(
        @Grab(group='javax.mail', module='mail', version='1.4.7')
)

import javax.mail.*
import javax.mail.internet.*
import java.util.Properties

/**
 * Created by maleficarum on 16/02/15.
 */
class MailSender {

    String  gmailServer = "imap.gmail.com"
    def pref = new Properties();
    def session
    def transport

    def MailSender() {
        loadPreferences()
        def props = new Properties();
        props.put("mail.smtp.starttls.enable",true);
        props.setProperty("mail.smtp.ssl.trust", gmailServer);
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.host", gmailServer);
        props.put("mail.smtp.user", pref["username"]);
        props.put("mail.smtp.password", pref["password"]);
        props.put("mail.smtp.port", "587");

        session = Session.getDefaultInstance(props,null)

    }

    def loadPreferences() {
        new File("config.properties").withInputStream {
            stream -> pref.load(stream)
        }
    }

    def sendMessage() {
        def to = pref["to"]
        def subject = pref["title"]
        def body = pref["message"]

        def toAddress = new InternetAddress(to);
        def ccAddress = new InternetAddress(pref["username"]);

        def message = new MimeMessage(session);
        message.setFrom(new InternetAddress(pref["username"]));
        message.addRecipient(Message.RecipientType.TO, toAddress);
        message.addRecipient(Message.RecipientType.CC, ccAddress );

        message.setSubject(subject);
        message.setText(body);

        transport = session.getTransport("smtp");

        transport.connect(gmailServer, pref["username"], pref["password"]);
        transport.sendMessage(message, message.getAllRecipients());
        transport.close();
    }

    def static void main(args) {
        def client = new MailSender()
        client.sendMessage()
    }
}
