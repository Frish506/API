package PhoneFeature;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Say;
import com.twilio.twiml.voice.Say.Language;
import com.twilio.type.PhoneNumber;

import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.post;

public class Phone {
    public static final String ACCOUNT_SID = "ACed7101dcd411895661d685366750155f";
    public static final String AUTH_TOKEN = "5f5f3f595368183fa592303cc13a3707";
    private String toNumber;

    public Phone(String toNumber) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        this.toNumber = toNumber;
    }
        // @WONG HMU
    public void sendMessage(String messageText){
        Message message = Message.creator(new PhoneNumber(toNumber),
                new PhoneNumber("+19783064143"),
                messageText).create();

        System.out.println(message.getSid());
    }

    public void sendCall(String messageAudio){
        String from = "+19783064143";
        try {
            publishXMLlocally(messageAudio);
            Call call = Call.creator(new PhoneNumber(toNumber), new PhoneNumber(from),
                    new URI("https://testatesterab.localtunnel.me/audio")).create();
            System.out.println(call.getSid());
        }
        catch (URISyntaxException e){
            System.out.println("Error!");
        }
    }


    private void publishXMLlocally(String message){
        post("/audio", (req, res) -> {
            Say say = new Say.Builder(message).voice(Say.Voice.ALICE)
                    .language(Language.EN_US).build();
            VoiceResponse response = new VoiceResponse.Builder().say(say).build();
            return response.toXml();
        });
    }


}