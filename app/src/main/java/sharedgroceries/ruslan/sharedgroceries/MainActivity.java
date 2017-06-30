package sharedgroceries.ruslan.sharedgroceries;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference ref;
    private ArrayList<Item> items;
    private ArrayList<String> history;
    private ArrayAdapter<String> historyAdapter;

    private ListView listView;
    private Button addButton;
    private Button sortTitleButton;
    private Button sortDateButton;
    private MultiAutoCompleteTextView addEditText;
    private ItemListAdapter adapter;

    private boolean compareDiraction;

    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        compareDiraction = false;
        handler = new Handler(getMainLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                synchronized (adapter) {
                    adapter.notifyDataSetChanged();
                }
            }
        };
        items = new ArrayList<>();
        history = new ArrayList<>();
        addButton = (Button) findViewById(R.id.addButton);
        sortDateButton = (Button) findViewById(R.id.sortDateButton);
        sortTitleButton = (Button) findViewById(R.id.sortTitleButton);

        listView = (ListView) findViewById(R.id.listView);
        adapter = new ItemListAdapter(this, items);
        listView.setAdapter(adapter);

        addEditText = (MultiAutoCompleteTextView) findViewById(R.id.addEditText);
        historyAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, history);
        addEditText.setThreshold(3);
        addEditText.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        addEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        addEditText.setAdapter(historyAdapter);
        addEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count > 0){
                    addEditText.showDropDown();
                }else{
                    addEditText.dismissDropDown();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string = addEditText.getText().toString().toLowerCase();
                Item item = null;
                if(!string.isEmpty()){
                    for(Item i : items){
                        if(i.title.equals(string)){
                            item = i;
                            break;
                        }
                    }
                    if(item != null){
                        item.count++;
                        item.timeStamp = Item.timeStampString(System.currentTimeMillis());
                        ref.child("items").child(string).setValue(item);
                    }else{
                        item = new Item(1, Item.timeStampString(System.currentTimeMillis()), string);
                        ref.child("items").child(string).setValue(item);
                    }
                    ref.child("history").child(string).setValue(string);
                }
            }
        });

        sortTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByTitle();
                compareDiraction = !compareDiraction;
                handler.post(runnable);
            }
        });

        sortDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sortByDate();
                compareDiraction = !compareDiraction;
                handler.post(runnable);
            }
        });

        setupFirebaseListener();
    }

    private void setupFirebaseListener(){
        ref = FirebaseDatabase.getInstance().getReference();
        ref.child("items").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Item item = dataSnapshot.getValue(Item.class);
                items.add(item);
                handler.post(runnable);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Item item = dataSnapshot.getValue(Item.class);
                for(Item i : items){
                    if(i.title.equals(item.title)){
                        i.count = item.count;
                        System.out.println(item.count);
                        i.timeStamp = item.timeStamp;
                        handler.post(runnable);
                        return;
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Item item = dataSnapshot.getValue(Item.class);
                for(Item i : items){
                    if(i.title.equals(item.title)){
                        items.remove(i);
                        handler.post(runnable);
                        return;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ref.child("history").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                history.add((String) dataSnapshot.getValue());
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                for(String str : history){
                    if(str.equals(dataSnapshot.getValue(String.class))){
                        history.remove(str);
                        historyAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void sortByDate(){
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                long obj1 = Item.timeStampLong(o1.timeStamp);
                long obj2 = Item.timeStampLong(o2.timeStamp);
                int res = compareDiraction ? (int)(obj1-obj2) : (int)(obj2-obj1);
                System.out.println(res);
                return res;
            }
        });
    }

    private void sortByTitle(){
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                String obj1 = o1.title;
                String obj2 = o2.title;
                int res =  compareDiraction ? obj1.compareTo(obj2) : obj2.compareTo(obj1);
                System.out.println(res);
                return res;
            }
        });
    }

    public void removeItem(int index){
        Item item = items.get(index);
        if(items.get(index).count > 1) {
            item.count--;
            ref.child("items").child(item.title).child("count").setValue(item.count);
        }
        else {
            ref.child("items").child(item.title).removeValue();
        }
    }
}
