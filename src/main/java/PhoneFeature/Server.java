package PhoneFeature;

import Database.SpillAPI.SpillManager;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.messaging.Body;
import com.twilio.twiml.messaging.Message;

import javax.servlet.http.HttpServlet;

import static spark.Spark.get;
import static spark.Spark.post;

public class Server extends HttpServlet {

    public void runServer(){

        get("/", (req, res) -> "Hello Web");

        post("/sms", (req, res) -> {
            res.type("application/xml");
            String returnString;
            String responseString = req.body();
            responseString = findStringBody(responseString); //take only relevant substring
            if(isValidInput(responseString)){
                //parse string for id and command
                int id = findRequestId(responseString);
                String cmd = findRequestCommand(responseString);

                if(isValidID(id)){
                    //update status of spill request based on text response
                    returnString = updateSpillStatusText(cmd, id);
                }
                else{
                    returnString = "Id not found. Please reenter request with valid id";
                }

            }
            else {
                returnString = "Invalid Input. Please try again.";
            }


            Body body = new Body
                    .Builder(returnString)
                    .build();
            Message sms = new Message
                    .Builder()
                    .body(body)
                    .build();
            MessagingResponse twiml = new MessagingResponse
                    .Builder()
                    .message(sms)
                    .build();
            return twiml.toXml();
        });
    }


    /**
     *
     * @param newStatus contains reply string from message
     * @return String with desired response to message based on param
     */
    public String updateSpillStatusText(String newStatus, int id){
        String returnString;
        //if response includes in progress status update
        if(newStatus.contains("DONE") || newStatus.contains("done") || newStatus.contains("Done")){

            //update spill in database
            SpillManager s = SpillManager.getSpillMangager();
            if(s.updateSpill(id, null, null, "done",null,null,null, null)) {
                //create response string for user
                returnString = "Spill status successfully updated to Done. " +
                        "Please reply to the following question as accurately as possible" +
                        " for our records: Approximately how long did it take you to clean" +
                        " this spill (in minutes)? Again, please reply with the spill ID followed by your response";
                Phone aPhone = new Phone("+17812547256");
                aPhone.sendMessage("Service request " + id + " has been marked as completed by a staff member.");
            }
            else {
                returnString = "Unable to complete report. Please enter a valid status";
            }

        }
        //if response includes in progress status update
        else if(newStatus.contains("Progress") || newStatus.contains("progress") || newStatus.contains("PROGRESS")){
            //update spill in database
            SpillManager s = SpillManager.getSpillMangager();
            if(s.updateSpill(id, null, null, "in progress",null,null,null, null)) {
                //create response string for user
                returnString = "Status successfully updated to In Progress";
            }
            else {
                returnString = "Unable to complete report. Please enter a valid status";
            }
        }
        //if response contains digits (suggests completion time)
        else if(newStatus.contains("0") || newStatus.contains("1") || newStatus.contains("2") || newStatus.contains("3") || newStatus.contains("4") || newStatus.contains("5") || newStatus.contains("6") || newStatus.contains("7") || newStatus.contains("8") || newStatus.contains("9")){
            //update spill report in database
            SpillManager s = SpillManager.getSpillMangager();
            if(s.generateReport(id, Integer.parseInt(newStatus))){
                //create response string for user
                newStatus = parseForNumber(newStatus);
                returnString = "Completion time recorded as " + newStatus + " minutes. Task complete.";
            }
            else {
                returnString = "Unable to complete report. Please enter valid data for time taken";
            }
        }
        else {
            returnString = "Invalid response. No actions taken.";
        }
        return returnString;
    }

    /**
     *
     * @param a string to be parsed
     * @return only user response from twilio text message
     */
    public String findStringBody(String a){
        String body;
        String[] parts = a.split("Body="); //splits string in two at body parameter
        String[] parts2 = parts[1].split("&FromCountry"); //splits string again at from country parameter
        body = parts2[0]; //this piece of a string should have only the text body
        return body;
    }

    /**
     *
     * @param a string to be parsed
     * @return only number entered by the user
     */
    private String parseForNumber(String a){
        String retString;
        if(a.contains("+") && (a.charAt(0) != '+')){
            a = a.replace("+", " ");
            String[] parts = a.split(" ");
            retString = parts[0];
        }
        else{
            retString = a;
        }

        return retString;
    }

    /**
     *
     * @param a string to be parsed
     * @return number representing request id in question
     */
    public int findRequestId(String a){
        a = a.replace("+", " ");
        String[] parts = a.split(" ");
        int id = Integer.parseInt(parts[0]);
        return id;
    }

    /**
     *
     * @param a string to be parsed
     * @return number representing request id in question
     */
    public String findRequestCommand(String a){
        a = a.replace("+", " ");
        String[] parts = a.split(" ");
        return parts[1];
    }

    /**
     *
     * @param a string to be checked
     * @return boolean indicating whether string is valid
     */
    public boolean isValidInput(String a){
        if(!a.contains("+")){
            return false;
        }
        a = a.replace("+", " ");
        String[] parts = a.split(" ");

        if(parts.length != 2){
            return false;
        }
        return true;
    }

    /**
     *
     * @param id from input request
     * @return whether id exists in spill database
     */
    public boolean isValidID(int id){
        SpillManager s = SpillManager.getSpillMangager();
        if(s.hasSpill(id)) {
            return true;
        }
        return false;
    }

}