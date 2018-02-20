package fr.elominp.yeahrand;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.provider.ContactsContract;
import android.net.Uri;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;

public class YeahRandMain extends AppCompatActivity {
    private class PhoneEntry {
        public String phoneNumber;
        public String name;
        public Boolean isChecked;

        public PhoneEntry(String n_name, String n_phone) {
            phoneNumber = n_phone;
            name = n_name;
            isChecked = false;
        }
    }

    private class PhoneAdapter extends ArrayAdapter<PhoneEntry> {
        public class PhoneViewHolder {
            public TextView name;
            public TextView phoneNumber;
            public CheckBox isChecked;
        }

        private List<PhoneEntry> _phoneEntries;

        public PhoneAdapter(Context context, List<PhoneEntry> phoneEntries) {
            super(context, 0, phoneEntries);
            _phoneEntries = phoneEntries;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.contacts_list_item, parent, false);
            }
            PhoneViewHolder holder = (PhoneViewHolder)convertView.getTag();
            if (holder == null) {
                holder = new PhoneViewHolder();
                holder.name = (TextView)convertView.findViewById(R.id.name);
                holder.phoneNumber = (TextView)convertView.findViewById(R.id.phoneNumber);
                holder.isChecked = (CheckBox)convertView.findViewById(R.id.checkBox);
                convertView.setTag(holder);
            }
            PhoneEntry entry = getItem(position);
            holder.name.setText(entry.name);
            holder.phoneNumber.setText(entry.phoneNumber);
            holder.isChecked.setChecked(entry.isChecked);
            return convertView;
        }

        @Override
        public PhoneEntry getItem(int position) {
            return _phoneEntries.get(position);
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }
    }

    public String[] messages = {
            "hello world",
            "salut",
            "plop",
            "yo",
            "yo!",
            "lol",
            "mdr",
            "ptdr",
            "xptdr",
            "Philipe! Je sais où tu te caches!",
            "Honk",
            "=",
            "ded",
            "Just Do It!",
            "Mec! Your dreams come true!",
            "Coucou",
            "Wololo",
            "Je suis ton père!"
    };

    protected void initUI() {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
            // Load an ad into the AdMob banner view.
            MobileAds.initialize(getApplicationContext(), "ca-app-pub-8052216586469752~8948622025");
            AdView adView = (AdView) findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);

            ListView listView = (ListView) findViewById(R.id.listView);
            ContentResolver resolver = getContentResolver();
            Cursor phones = resolver.query(ContactsContract.Contacts.CONTENT_URI, null,
                    null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            List<PhoneEntry> numbersList = new ArrayList<PhoneEntry>();
            if (phones != null && phones.getCount() > 0) {
                phones.moveToFirst();
                try {
                    do {
                        String name = phones.getString(phones.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME));
                        String id = phones.getString(phones.getColumnIndex(
                                ContactsContract.Contacts._ID));
                        if (Integer.parseInt(phones.getString(phones.getColumnIndex(
                                ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                            Cursor numbers = resolver.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                    new String[]{id}, null);
                            if (numbers != null && numbers.getCount() > 0) {
                                numbers.moveToFirst();
                                do {
                                    String number = numbers.getString(numbers.getColumnIndex(
                                            ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    /* if (numbers.getInt(
                                            numbers.getColumnIndex(
                                                    ContactsContract.CommonDataKinds.Phone.TYPE))
                                            == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)*/
                                    numbersList.add(new PhoneEntry(name, number));
                                }
                                while (numbers.moveToNext());
                                numbers.close();
                            }
                        }
                    }
                    while (phones.moveToNext());
                    phones.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            PhoneAdapter adapter = new PhoneAdapter(this, numbersList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    PhoneEntry entry = (PhoneEntry) adapterView.getItemAtPosition(i);
                    entry.isChecked = !entry.isChecked;
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
                    checkBox.setChecked(entry.isChecked);
                }
            });
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yeah_rand_main);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            android.Manifest.permission.READ_CONTACTS,
                            android.Manifest.permission.SEND_SMS
                    },
                    0
            );
        }

        initUI();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 0 &&
                grantResults.length == 2 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            initUI();
        }
    }

    /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_yeah_rand_main, menu);
        return true;
    } */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSendRandomMessages(View view) {
        ListView listView = (ListView)findViewById(R.id.listView);
        ListAdapter adapter = listView.getAdapter();
        try {
            SmsManager manager = SmsManager.getDefault();
            for (int i = 0; i < adapter.getCount(); i++) {
                PhoneEntry entry = (PhoneEntry)adapter.getItem(i);
                if (entry.isChecked) {
                    int idx = Double.valueOf(Math.random() * (double)(messages.length)).intValue();
                    if (idx == messages.length)
                        idx--;
                    manager.sendTextMessage(entry.phoneNumber, null, messages[idx], null, null);
                    Toast.makeText(
                            this,
                            entry.name + " a reçu : " + messages[idx],
                            Toast.LENGTH_SHORT
                    ).show();;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}
