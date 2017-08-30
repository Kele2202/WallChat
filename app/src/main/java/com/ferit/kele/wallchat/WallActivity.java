package com.ferit.kele.wallchat;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

public class WallActivity extends AppCompatActivity {

    ListView lvMessages;
    ImageButton ibSend, ibCamera, ibLoadPic;
    EditText etTextInput;
    DatabaseReference myRef;
    private String nickname;
    Context context;
    private static  final int loadPicture = 1, takePicture = 1;
    boolean takeFlag = false, loadFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);
        setUpUI();
    }

        private void setUpUI() {
            this.lvMessages = (ListView) this.findViewById(R.id.lvMessages);
            this.ibSend = (ImageButton) this.findViewById(R.id.ibSend);
            this.ibCamera = (ImageButton) this.findViewById(R.id.ibCamera);
            this.etTextInput = (EditText) this.findViewById(R.id.etTextInput);
            this.ibLoadPic = (ImageButton) this.findViewById(R.id.ibLoadPIC);

            final MsgAdapter msgAdapter = new MsgAdapter(showMessages());
            this.lvMessages.setAdapter(msgAdapter);

            myRef = FirebaseDatabase.getInstance().getReference().getRoot().child("WallChat-Room");
            nickname = getIntent().getExtras().get("nickname").toString();

            ibSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Map map = new HashMap();
                    map.put("Nickname", nickname);
                    map.put("TypeTXT", 1);
                    map.put("Msg", etTextInput.getText().toString().trim());
                    map.put("Time", getTime());
                    etTextInput.setText("");
                    context = getApplicationContext();
                    SendMsg sendMsg = new SendMsg(context, map);
                    sendMsg.execute();
                    }
            });

            ibLoadPic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadFlag = true;
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, loadPicture);
                }
            });

            ibCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takeFlag = true;
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager())!=null){
                        startActivityForResult(intent, takePicture);
                    }
                }
            });

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    msgAdapter.clear();
                    Iterator i = dataSnapshot.getChildren().iterator();
                    while (i.hasNext()){
                        DataSnapshot ds1 = dataSnapshot.child(((DataSnapshot)i.next()).getKey());
                        Iterator j = ds1.getChildren().iterator();
                        while(j.hasNext())
                        {
                            String location = (String) ((DataSnapshot) j.next()).getValue();
                            String message = (String) ((DataSnapshot) j.next()).getValue();
                            String nickname = (String) ((DataSnapshot) j.next()).getValue();
                            String time = (String) ((DataSnapshot) j.next()).getValue();
                            int type = Integer.parseInt(String.valueOf(((DataSnapshot) j.next()).getValue()));
                            msgAdapter.add(new Msg(nickname, message, time, location, type));
                            lvMessages.smoothScrollToPosition(msgAdapter.getCount()-1);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
    }

    private ArrayList<Msg> showMessages() {

        ArrayList<Msg> messages = new ArrayList<>();

        return messages;
    }

    public String getTime() {
        String time, hour, minute;
        String[] sID = TimeZone.getAvailableIDs(2*3600000);
        SimpleTimeZone timeZone = new SimpleTimeZone(2*3600000, sID[0]);
        Calendar calendar = new GregorianCalendar(timeZone);
        hour = String.valueOf((String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) == "0") ? ("0" + calendar.get(Calendar.HOUR_OF_DAY)) : (calendar.get(Calendar.HOUR_OF_DAY)));
        minute = String.valueOf((String.valueOf(calendar.get(Calendar.MINUTE)).length()== 1) ? ("0" + calendar.get(Calendar.MINUTE)) : calendar.get(Calendar.MINUTE));
        time = hour + ":" + minute;
        return time;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (loadFlag){
            loadFlag = false;
            switch (requestCode){
                case loadPicture:
                    if (resultCode==RESULT_OK){
                        Uri uri = data.getData();
                        String[]projection = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(projection[0]);
                        String filePath = cursor.getString(columnIndex);
                        cursor.close();

                        Bitmap image = BitmapFactory.decodeFile(filePath);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                        byte[] bytes = baos.toByteArray();
                        String base64Img = Base64.encodeToString(bytes, Base64.DEFAULT);

                        Map map = new HashMap();
                        map.put("Nickname", nickname);
                        map.put("TypeTXT", 0);
                        map.put("Msg", base64Img);
                        map.put("Time", getTime());
                        context = getApplicationContext();
                        SendMsg sendMsg = new SendMsg(context, map);
                        sendMsg.execute();
                    }
                    break;
            }
        }
        if (takeFlag){
            takeFlag = false;
            if (requestCode == takePicture && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap image = (Bitmap) extras.get("data");

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 65, baos);
                byte[] bytes = baos.toByteArray();
                String base64Img = Base64.encodeToString(bytes, Base64.DEFAULT);

                Map map = new HashMap();
                map.put("Nickname", nickname);
                map.put("TypeTXT", 0);
                map.put("Msg", base64Img);
                map.put("Time", getTime());
                context = getApplicationContext();
                SendMsg sendMsg = new SendMsg(context, map);
                sendMsg.execute();
            }

        }
    }
}
