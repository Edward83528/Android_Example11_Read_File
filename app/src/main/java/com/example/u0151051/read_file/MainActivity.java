package com.example.u0151051.read_file;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

//android是有規劃的通常是放在內部儲存記憶體(分1.自己的apk內和2.暫存區)和外部儲存記憶體(1.公用資料夾 例如 DCIM, Pictures …等2.私有資料夾 例如外部路徑的 Android 資料夾下)
//如果你是選擇儲存在內部只有你的程式才可以存取此檔案且當你的程式移除後, 該檔案也會跟著被移除儲存空間大小亦受限制
// 如果你儲存在外部儲存空間則無法對該檔案做一切權限上的管理當程式移除後資料並不會隨之消失
//儲存在外部空間必須設定存取權限(在AndroidManifest.xml加上 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />和 <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />)
//以上權限通常預設會是允許讀取但是當你宣告寫入權限時預設讀取權限也會被打開但是避免後面版本有所變更建議兩者還是都開啟比較
public class MainActivity extends AppCompatActivity {
    TextView tv, tv4, tv5;
    Button btn1, btn2, btn4, btn5, btn6, btn7;
    File f;
    String fileName = "my_file";
    String data = "我如果有翅膀";
    String data2 = "這是內部暫存";
    String data3 = "這是外部公開資料夾";
    String data4 = "這是外部私有資料夾";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findview();
    }

    void findview() {
        tv = (TextView) findViewById(R.id.textView3);
        tv4 = (TextView) findViewById(R.id.textView4);
        tv5 = (TextView) findViewById(R.id.textView5);
        btn1 = (Button) findViewById(R.id.button2);
        btn2 = (Button) findViewById(R.id.button3);
        btn4 = (Button) findViewById(R.id.button4);
        btn5 = (Button) findViewById(R.id.button5);
        btn6 = (Button) findViewById(R.id.button6);
        btn7 = (Button) findViewById(R.id.button7);
        btn1.setOnClickListener(c);
        btn2.setOnClickListener(c);
        btn4.setOnClickListener(c);
        btn5.setOnClickListener(c);
        btn6.setOnClickListener(c);
        btn7.setOnClickListener(c);
    }

    //建立監聽器
    View.OnClickListener c = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button2:
                    internal_write();
                    break;
                case R.id.button3:
                    internal_read();
                    break;
                case R.id.button4:
                    internal_write_temporary();
                    break;
                case R.id.button5:
                    internal_read_temporary();
                    break;
                //如果要將檔案寫入外部儲存空間,最好先判斷一下外部儲存空間是否可讀寫?也要判斷外部空間是否可以讀取
                case R.id.button6:
                    if (isExternalStorageWritable()) {
                        //可寫
                        external_public();
                    } else if (isExternalStorageReadable()) {
                        //可讀
                    } else {
                        //不可寫不可讀
                    }
                    break;
                case R.id.button7:
                    external_private();
                    break;
            }
        }
    };

    //內部檔案寫入方式
    void internal_write() {
        try {
            FileOutputStream f = openFileOutput(fileName, MODE_PRIVATE);
            f.write(data.getBytes());
            tv5.setText(getFilesDir().getPath());
            f.close();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "內部寫入出現錯誤", Toast.LENGTH_SHORT).show();
        }
    }

    //內部檔案讀取方式
    void internal_read() {
        try {
            FileInputStream fileInputStream = openFileInput(fileName);
            byte b[] = new byte[fileInputStream.available()];
            StringBuffer stringBuffer = new StringBuffer();
            while (fileInputStream.read(b) != -1) {
                stringBuffer.append(new String(b));
            }
            tv.setText(stringBuffer.toString());
            tv5.setText(getFilesDir().getPath());
            fileInputStream.close();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "內部讀取出現錯誤", Toast.LENGTH_SHORT).show();
        }
    }

    //內部暫存檔案寫入方式
    void internal_write_temporary() {
        //如果要存到暫存資料夾要透過getCacheDir這個方法取得路徑,並且透過 createTempFile 方法建立檔案,透過這個方法會在apk下cache 資料夾下產生檔案
        //catch資料夾會根據檔案大小以及目前所能配置的記憶體大小來動態配置空間,當系統記憶體不夠配置的時候, 會自動刪除該資料夾內的檔案,如果暫時存放的檔案可以透過這個方式進行存放。
        try {
            f = File.createTempFile(fileName, null, getCacheDir());
            FileOutputStream fileOutputStream = new FileOutputStream(f);
            fileOutputStream.write(data2.getBytes());
            tv5.setText(getFilesDir().getPath());
            fileOutputStream.close();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "內部暫存寫入出現錯誤", Toast.LENGTH_SHORT).show();
        }
    }

    //內部暫存檔案讀取方式
    void internal_read_temporary() {
        try {
            if (f == null) {
                return;
            }
            FileInputStream fileInputStream = new FileInputStream(f);
            byte b[] = new byte[fileInputStream.available()];
            StringBuffer stringBuffer = new StringBuffer();
            while (fileInputStream.read(b) != -1) {
                stringBuffer.append(new String(b));
            }
            tv.setText(stringBuffer.toString());
            tv5.setText(getFilesDir().getPath());
            fileInputStream.close();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "內部暫存讀取出現錯誤", Toast.LENGTH_SHORT).show();
        }
    }

    //外部空間建立公開資料夾
    void external_public() {
        File dir = getExtermalStoragePrivateDir("bb");
        File f = new File(dir, fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            outputStream.write(data3.getBytes());
            tv5.setText(getFilesDir().getPath());
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "外部寫入公開資料夾出現問題", Toast.LENGTH_SHORT).show();
        }

    }

    //外部空間建立私有資料夾(如果你想在 app 被刪除時, 儲存的檔案一起刪除, 則使用外部私有資料夾,否則使用公用資料夾會是比較恰當。)
    void external_private() {
        File dir = getExtermalStoragePublicDir("aa");
        File f = new File(dir.getPath(), fileName);
        try {
            FileOutputStream outputStream = new FileOutputStream(f);
            outputStream.write(data4.getBytes());
            tv5.setText(getFilesDir().getPath());
            outputStream.close();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "外部寫入私有資料夾出現問題", Toast.LENGTH_SHORT).show();
        }
    }

    //由於每支手機的外部儲存空間都不一樣,因此你不能把路徑寫死,必須透過以下程式碼進行存取
    private File getExtermalStoragePublicDir(String albumName) {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (file.mkdir()) {
            File f = new File(file, albumName);
            if (f.mkdir()) {
                return f;
            }
        }
        return new File(file, albumName);
    }

    //由於每支手機的外部儲存空間都不一樣,因此你不能把路徑寫死,必須透過以下程式碼進行存取
    private File getExtermalStoragePrivateDir(String albumName) {
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.e("", "Directory not created or exist");
        }
        return file;
    }

    //如果要將檔案寫入外部儲存空間最好先判斷一下外部儲存空間是否可讀寫?
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    //也要判斷外部空間是否可以讀取
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

}
