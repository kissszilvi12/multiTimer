package gerida.timer;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import static gerida.timer.CountDown.isPaused;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private CountDown[] countDowns = new CountDown[6];
    private Button[] gamers = new Button[6];
    private Button[] cancels = new Button[6];
    private Button pauseButton, resetButton;
    private TextView time;
    private int count = 30;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // Start buttons
        String id = "gamer";
        for (int i = 0; i < 6; i++) {
            int resID = getResources().getIdentifier(id + (i + 1), "id", getPackageName());
            gamers[i] = findViewById(resID);
            gamers[i].setOnClickListener(this);
            gamers[i].setOnLongClickListener(this);
        }

        // Cancel buttons
        id = "cancel";
        for (int i = 0; i < 6; i++) {
            int resID = getResources().getIdentifier(id + (i + 1), "id", getPackageName());
            cancels[i] = findViewById(resID);
            cancels[i].setOnClickListener(this);
            cancels[i].setEnabled(false);
        }

        // Count down timer
        for (int i = 0; i < 6; i++){
            countDowns[i] = new CountDown(count, gamers[i], cancels[i], vibrator);
        }

        //Time text view
        time = findViewById(R.id.timerTextView);
        updateText(time);
        time.setOnLongClickListener(this);

        //Reset button
        resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(this);

        // Pause/Resume button
        pauseButton = findViewById(R.id.pauseButton);
        pauseButton.setOnClickListener(this);
    }

    @Override
    public boolean onLongClick(View v) {
        if(v.getId()==time.getId()){
            return showNumberPickerDialog();
        }
        Button b = findViewById(v.getId());

        int i=0;
        boolean found = false;
        //search gamer button
        while(!found && i<6){
            if(gamers[i].getId() == v.getId())
                found=true;
            i++;
        }
        if(found) {
            i--;
            return showTextDialog(b, i);
        }else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(final View v) {
        //timer
        switch(v.getId()){
    //Handle reset button click
            case R.id.resetButton:

                if(isStarted()){
                    for(int i = 0; i < 6; i++) {
                        if (countDowns[i].isStarted())
                            countDowns[i].cancel();
                    }
                    pauseButton.setText(R.string.szunet);
                }
                return;
    //Handle pause button click
            case R.id.pauseButton:
                if(!isPaused() && isStarted()) {    //If the user doesn't paused, and started the timer, pause
                        CountDown.pause();
                        pauseButton.setText(R.string.folytatas);
                }
                else if(isPaused()){    //If the user paused the timer, the button is Continue button
                    for (int i = 0; i < 6; i++) {
                        countDowns[i].resume();
                        pauseButton.setText(R.string.szunet);
                    }
                }
                return;
        }

    //Handle gamer and cancel button
        boolean found=false;
        int i=0;
        //Search gamer button with linear search
        while(!found && i<6){
            if(gamers[i].getId() == v.getId())
                found=true;
            i++;
        }
        if(found){
            i--;
            countDowns[i].start(count*1000);
        }
        //Search cancel button with linear search
        else {
            i=0;
            while(!found && i<6){
                if(cancels[i].getId() == v.getId())
                    found=true;
                i++;
            }
            if(found){
                i--;
                countDowns[i].cancel();
                if(!(isStarted()) && isPaused()){
                    pauseButton.setText(R.string.szunet);
                }
            }
        }
    }

    private boolean showTextDialog(final Button b, final int i){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Játékos neve");

        final EditText setName = new EditText(this);
        String replaced = b.getText().toString().replace("|", ",");
        String[] splittedName = replaced.split(",");
        setName.setText(splittedName[0].trim());
        setName.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(setName);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newName = setName.getText().toString().trim();
                b.setText(newName);
                countDowns[i].setName(newName);
                showColorDialog(b);
            }
        });

        builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        //create new countDown with new name
        //countDowns[i].setName(gamers[i].getText().toString());

        return builder.show().isShowing();
    }

    private void showColorDialog(final Button b){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Játékos színe");

        final String[] colors = new String[]{"piros","kék","zöld","sárga","lila"};
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0: //red
                        b.setBackgroundColor(Color.rgb(255,200,200));
                        break;
                    case 1: //blue
                        b.setBackgroundColor(Color.rgb(200,200,255));
                        break;
                    case 2: //green
                        b.setBackgroundColor(Color.rgb(200,255,200));
                        break;
                    case 3: //yellow
                        b.setBackgroundColor(Color.rgb(249,211,66));
                        break;
                    case 4: //purple
                        b.setBackgroundColor(Color.rgb(200,100,200));
                        break;
                }
            }
        });

        builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private boolean showNumberPickerDialog(){
        final NumberPicker np = new NumberPicker(this);
        np.setMinValue(20);
        np.setMaxValue(35);
        np.setValue(count);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Gugolás");
        builder.setMessage("Válaszd ki hány másodperc legyen:");
        builder.setView(np);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                count = np.getValue();
                updateText(time);
            }
        });

        return builder.show().isShowing();
    }

    @SuppressLint("SetTextI18n")
    private void updateText(TextView tv){
        tv.setText(count+" másodperc");
    }

    private boolean isStarted(){
        boolean isStarted = false;
        int i = 0;
        while(!isStarted && i < 6){
            isStarted = countDowns[i].isStarted();
            i++;
        }

        return isStarted;
    }
}
