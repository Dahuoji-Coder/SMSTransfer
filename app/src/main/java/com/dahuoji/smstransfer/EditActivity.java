package com.dahuoji.smstransfer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.dahuoji.smstransfer.database.DBUtil;

import org.w3c.dom.Text;

public class EditActivity extends BaseActivity implements View.OnClickListener {

    private DBUtil dbUtil;
    private TextView phoneNumber1TextView;
    private TextView phoneNumber2TextView;
    private CaseEntity caseEntity = null;
    private EditText filtersPhoneNumberInput;
    private EditText filtersKeyword1Input;
    private EditText filtersKeyword2Input;
    public static final String Table_Name = "table_sms_transfer";
    public static final String Column_ID = "_id";
    public static final String Column_Filters_Phone_Number = "filters_phone_number";
    public static final String Column_Filters_Keyword_1 = "filters_keyword_1";
    public static final String Column_Filters_Keyword_2 = "filters_keyword_2";
    public static final String Column_Forward_Contact_1 = "forward_contact_1";
    public static final String Column_Forward_Contact_2 = "forward_contact_2";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        View phoneNumber1Layout = findViewById(R.id.phoneNumber1Layout);
        View phoneNumber2Layout = findViewById(R.id.phoneNumber2Layout);
        ListActivity.setAlphaChange(phoneNumber1Layout, phoneNumber2Layout);
        phoneNumber1Layout.setOnClickListener(this);
        phoneNumber2Layout.setOnClickListener(this);
        filtersPhoneNumberInput = findViewById(R.id.filtersPhoneNumberInput);
        filtersKeyword1Input = findViewById(R.id.filtersKeyword1Input);
        filtersKeyword2Input = findViewById(R.id.filtersKeyword2Input);
        dbUtil = DBUtil.getInstance(this);
        phoneNumber1TextView = findViewById(R.id.phoneNumber1TextView);
        phoneNumber2TextView = findViewById(R.id.phoneNumber2TextView);
        findViewById(R.id.buttonSave).setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            caseEntity = (CaseEntity) extras.getSerializable("case_entity");
        }
        if (caseEntity != null) {
            if (!TextUtils.isEmpty(caseEntity.getFiltersPhoneNumber())) {
                filtersPhoneNumberInput.setText(caseEntity.getFiltersPhoneNumber());
            }
            if (!TextUtils.isEmpty(caseEntity.getFiltersKeyword1())) {
                filtersKeyword1Input.setText(caseEntity.getFiltersKeyword1());
            }
            if (!TextUtils.isEmpty(caseEntity.getFiltersKeyword2())) {
                filtersKeyword2Input.setText(caseEntity.getFiltersKeyword2());
            }
            if (caseEntity.getContact1() != null) {
                phoneNumber1TextView.setText(caseEntity.getContact1().getName() + " (" + caseEntity.getContact1().getPhoneNumber() + ")");
                phoneNumber1TextView.setTag(caseEntity.getContact1().getName() + "," + caseEntity.getContact1().getPhoneNumber());
            }
            if (caseEntity.getContact2() != null) {
                phoneNumber2TextView.setText(caseEntity.getContact2().getName() + " (" + caseEntity.getContact2().getPhoneNumber() + ")");
                phoneNumber2TextView.setTag(caseEntity.getContact2().getName() + "," + caseEntity.getContact2().getPhoneNumber());
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.phoneNumber1Layout) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent, 1001);
        } else if (v.getId() == R.id.phoneNumber2Layout) {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
            startActivityForResult(intent, 1002);
        } else if (v.getId() == R.id.buttonSave) {
            String filtersPhoneNumber = filtersPhoneNumberInput.getText().toString().trim();
            String filtersKeyword1 = filtersKeyword1Input.getText().toString().trim();
            String filtersKeyword2 = filtersKeyword2Input.getText().toString().trim();
            String contact1 = "";
            if (phoneNumber1TextView.getTag() != null) {
                contact1 = (String) phoneNumber1TextView.getTag();
            }
            String contact2 = "";
            if (phoneNumber2TextView.getTag() != null) {
                contact2 = (String) phoneNumber2TextView.getTag();
            }
            if (!dbUtil.isExists(Table_Name)) {
                dbUtil.createTable(Table_Name, new String[]{Column_ID, Column_Filters_Phone_Number, Column_Filters_Keyword_1, Column_Filters_Keyword_2, Column_Forward_Contact_1, Column_Forward_Contact_2});
            }
            if (!TextUtils.isEmpty(contact1) || !TextUtils.isEmpty(contact2)) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(Column_Filters_Phone_Number, filtersPhoneNumber);
                contentValues.put(Column_Filters_Keyword_1, filtersKeyword1);
                contentValues.put(Column_Filters_Keyword_2, filtersKeyword2);
                contentValues.put(Column_Forward_Contact_1, contact1);
                contentValues.put(Column_Forward_Contact_2, contact2);
                if (caseEntity != null) {
                    contentValues.put(Column_ID, caseEntity.getId());
                    dbUtil.updateData(Table_Name, contentValues, Column_ID + "=?", new String[]{caseEntity.getId()});
                } else {
                    contentValues.put(Column_ID, "" + System.currentTimeMillis());
                    dbUtil.insertData(Table_Name, null, contentValues);
                }
            }
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 || requestCode == 1002) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    String[] contact = getPhoneContacts(uri);
                    if (contact != null) {
                        String name = contact[0];//姓名
                        String number = contact[1];//手机号
                        if (requestCode == 1001) {
                            phoneNumber1TextView.setText(name + " (" + number + ")");
                            phoneNumber1TextView.setTag(name + "," + number);
                        } else {
                            phoneNumber2TextView.setText(name + " (" + number + ")");
                            phoneNumber2TextView.setTag(name + "," + number);
                        }
                    }
                }
            }
        }
    }

    /**
     * 读取联系人信息
     */
    private String[] getPhoneContacts(Uri uri) {
        String[] contact = new String[2];
        //得到ContentResolver对象
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            //取得联系人姓名
            int nameFieldColumnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            contact[0] = cursor.getString(nameFieldColumnIndex);
            contact[1] = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Log.i("contacts", contact[0]);
            Log.i("contactsUsername", contact[1]);
            cursor.close();
        } else {
            return null;
        }
        return contact;
    }
}