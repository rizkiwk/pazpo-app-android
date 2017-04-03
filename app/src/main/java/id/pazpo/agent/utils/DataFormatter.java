package id.pazpo.agent.utils;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by adigunawan on 1/18/17.
 */

public class DataFormatter {

    public static String formatBudget(String budget) {
        NumberFormat nf     = NumberFormat.getInstance();
        nf.setGroupingUsed(true);
        double rawPrice     = Double.valueOf(budget);
        double thousands    = Double.valueOf(rawPrice)/1000;
        double millions     = thousands/1000;
        double billions     = millions/1000;
        double trillions    = billions/1000;
        switch (budget.length()) {
            case 1:
                budget = MessageFormat.format("Rp {0} ", nf.format(rawPrice));
                break;
            case 2:
                budget = MessageFormat.format("Rp {0} ", nf.format(rawPrice));
                break;
            case 3:
                budget = MessageFormat.format("Rp {0} ", nf.format(rawPrice));
                break;
            case 4:
                budget = MessageFormat.format("Rp {0}RB ", nf.format(thousands));
                break;
            case 5:
                budget = MessageFormat.format("Rp {0}RB ", nf.format(thousands));
                break;
            case 6:
                budget = MessageFormat.format("Rp {0}RB ", nf.format(thousands));
                break;
            case 7:
                budget = MessageFormat.format("Rp {0}JT ", nf.format(millions));
                break;
            case 8:
                budget = MessageFormat.format("Rp {0}JT ", nf.format(millions));
                break;
            case 9:
                budget = MessageFormat.format("Rp {0}JT ", nf.format(millions));
                break;
            case 10:
                budget = MessageFormat.format("Rp {0}M ", nf.format(billions));
                break;
            case 11:
                budget = MessageFormat.format("Rp {0}M ", nf.format(billions));
                break;
            case 12:
                budget = MessageFormat.format("Rp {0}M ", nf.format(billions));
                break;
            case 13:
                budget = MessageFormat.format("Rp {0}T ", nf.format(trillions));
                break;
            case 14:
                budget = MessageFormat.format("Rp {0}T ", nf.format(trillions));
                break;
            case 15:
                budget = MessageFormat.format("Rp {0}T ", nf.format(trillions));
                break;
        }

        return budget;
    }

    public static String formatTime(String transactTime, int type) {
        try {
            Date time           = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(transactTime);
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            Date pDate          = df.parse(transactTime);
            Date tDate          = df.parse(df.format(new Date()));
            if (pDate.before(tDate)) {
                if (type == 2) {
                    transactTime = new SimpleDateFormat("dd/MM/yy").format(time);
                } else {
                    transactTime = new SimpleDateFormat("dd").format(time) + " " +
                            new SimpleDateFormat("MMM").format(time) + " " +
                            new SimpleDateFormat("yyyy").format(time) + " " +
                            new SimpleDateFormat("HH:mm").format(time);
                }
            } else {
                switch (type) {
                    case 0:
                        transactTime = "Hari ini" + " " + new SimpleDateFormat("HH:mm").format(time);
                        break;
                    case 1:
                        transactTime = new SimpleDateFormat("HH:mm").format(time);
                        break;
                    case 2:
                        transactTime = new SimpleDateFormat("HH:mm").format(time);
                        break;
                }
            }
        } catch (ParseException e1) {
            e1.printStackTrace();
        }

        return transactTime;
    }

}
