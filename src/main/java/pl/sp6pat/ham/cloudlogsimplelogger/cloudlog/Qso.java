package pl.sp6pat.ham.cloudlogsimplelogger.cloudlog;

import lombok.Data;

@Data
public class Qso {

    private String date;
    private String time;
    private String mode;
    private String band;
    private String freq;
    private String call;
    private String rstS;
    private String rstR;
    private String name;
    private String location;
    private String comment;

}
