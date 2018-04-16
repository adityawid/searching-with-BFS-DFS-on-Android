package com.devjurnal.bfs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class DepthFirstSearchActivity extends AppCompatActivity {
    private RelativeLayout rl;
    private Button btn_add_root;
    private Button btn_add_vertex;
    private Button btn_remove_vertex;
    private EditText et_inputvalue;
    private Button btn_setvalue;
    private EditText et_searchkey;
    private Button btn_startdfs;
    private TextView tv_stack;

    private boolean flag_addRoot;
    private boolean flag_addVertex;
    private Vertex root;
    public Vertex selected;

    Paint paint;
    DrawView lines;
    String qStr = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depth_first_search);
        getSupportActionBar().setTitle(getString(R.string.action_DFS));
        rl = findViewById(R.id.relativeLayout2);
        btn_add_root = (Button) findViewById(R.id.add_root);
        btn_add_vertex = (Button) findViewById(R.id.button2);
        btn_remove_vertex = (Button) findViewById(R.id.button3);
        et_inputvalue = (EditText) findViewById(R.id.et_inputvalue);
        btn_setvalue = (Button) findViewById(R.id.btn_setvalue);
        et_searchkey = (EditText) findViewById(R.id.et_searchkey);
        btn_startdfs = (Button) findViewById(R.id.btn_startdfs);
        tv_stack = (TextView) findViewById(R.id.tv_queue);

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);

        lines = new DrawView(this);
        rl.addView(lines);

        flag_addRoot = false;
        flag_addVertex = false;

		/*
		 * bind enter key to run setVertexValue method while setting value of a
		 * vertex
		 */
        et_inputvalue.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    setVertexValue(v);
                }
                return false;
            }
        });

		/* bind enter key to run doBFS method after entering search key */
        et_searchkey.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    doDFS(v);
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_exit:
                System.exit(0);
                break;
            case R.id.action_DFS:
                Toast.makeText(this, "Anda di halaman DFS", Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_BFS:
                startActivity(new Intent(this,MainActivity.class));
                finish();
                break;
            default:
                break;
        }
        return true;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
		/* TODO addRoot and addVertex repeat the same code, merge them */
        if (flag_addRoot) {
			/* runs when adding a root vertex for the first time */
            flag_addRoot = false;
            Point p = new Point((int) event.getX(), (int) event.getY());

            root = new Vertex(0, getApplicationContext(), p);
            root.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selected != null)
                        selected.unselect();
                    btn_add_vertex.setEnabled(true);
                    btn_remove_vertex.setEnabled(true);
                    et_inputvalue.setEnabled(true);
                    btn_setvalue.setEnabled(true);
                    et_searchkey.setEnabled(true);
                    btn_startdfs.setEnabled(true);
                    selected = findVertexByTextView((TextView) v);
                    selected.select();
                    et_inputvalue.setText(String.valueOf(selected.getData()));
                }
            });
            rl.addView(root.tv);
        } else if (flag_addVertex) {
			/* runs when adding new child vertices */
            flag_addVertex = false;
            Point p = new Point((int) event.getX(), (int) event.getY());
            Vertex v = new Vertex(0, getApplicationContext(), p);
            selected.addChild(v);
            v.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selected != null)
                        selected.unselect();
                    btn_add_vertex.setEnabled(true);
                    btn_remove_vertex.setEnabled(true);
                    et_inputvalue.setEnabled(true);
                    btn_setvalue.setEnabled(true);
                    selected = findVertexByTextView((TextView) v);
                    selected.select();
                    et_inputvalue.setText(String.valueOf(selected.getData()));
                }
            });
            rl.addView(v.tv);
        } else {
			/* runs when touched on empty space */
            if (selected != null) {
                selected.unselect();
                selected = null;
                btn_add_vertex.setEnabled(false);
                btn_remove_vertex.setEnabled(false);
                et_inputvalue.setEnabled(false);
                btn_setvalue.setEnabled(false);
            }
        }
        return true;
    }

    /* adds starting vertex */
    public void addRoot(View v) {
        flag_addRoot = true;
        btn_add_root.setEnabled(false);
        Toast.makeText(this, R.string.toast_addroot, Toast.LENGTH_LONG).show();
    }

    /* adds child vertices for the chosen vertex */
    public void addVertex(View v) {
        flag_addVertex = true;
		/* TODO create a new string and add toast here */
    }

    /*
     * compares available textView's in the layout and returns owning Vertex
     * object if a match has been found
     */
    public Vertex findVertexByTextView(TextView tv) {
        Queue<Vertex> q = new LinkedList<Vertex>();
        Stack<Vertex> s = new Stack<>();
        s.add(this.root);
//        q.add(this.root);
        while (!s.isEmpty()) {
//            Vertex vq = q.remove();
            Vertex vs = s.pop();
            if (tv.equals(vs.tv))
                return vs;
            //                q.add(c);
            s.addAll(vs.getChildren());
        }
        return null;
    }

    /*
     * reads value from EditText and sets value of selected vertex
     */
    public void setVertexValue(View v) {
        int value = 0;
        try {
            value = Integer.parseInt(et_inputvalue.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.toast_emptyvalue,
                    Toast.LENGTH_LONG).show();
            return;
        }
        selected.setData(value);
    }

    /* breadth-first search is done here */
    public void doDFS(View v) {
        qStr = "";
        int key = 0;
        int cnt = 0;
        try {
            key = Integer.parseInt(et_searchkey.getText().toString());
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.toast_emptykey,
                    Toast.LENGTH_LONG).show();
            return;
        }
        resetTree();
        Stack<Vertex> s = new Stack<>();

        s.add(this.root);               // memasukkan nilai root ke dalam antian
        generateQStr(s);                // parse ke string & cetak antrian/queue

        while (!s.isEmpty()){
            Vertex actualVertex = s.pop();
            cnt++;
            if (key == actualVertex.getData()) {
                actualVertex.tv.setBackgroundColor(Color.RED);
                tv_stack.append("\nFound key=" + key + " in " + cnt + " steps.");
                return;
            }
            for (Vertex vertex : actualVertex.getChildren()){
                if(!vertex.getVisited()){
                    vertex.setVisited(true);
                    vertex.tv.setBackgroundColor(Color.GREEN);
                    s.push(vertex);
                    generateQStr(s);
                }
            }
        }
        tv_stack.append("\nCould not find key=" + key + "!");
    }

    /* sets all vertices as unvisited */
    public void resetTree() {
        Queue<Vertex> q = new LinkedList<Vertex>();
        q.add(this.root);
        while (!q.isEmpty()) {
            Vertex v = q.remove();
            v.setVisited(false);
            for (Vertex c : v.getChildren()) {
                q.add(c);
            }
        }
        tv_stack.setText("");
    }

    /*
     * generates queue string for each BFS enqueue / dequeue operation
     */
    public void generateQStr(Stack<Vertex> q) {
        Vertex v;
//       qStr = "";
        for (int i = 0; i < q.size() ; i++) {
            v = q.pop();
            Log.d("popStack-"+i,qStr);
            qStr = qStr.concat(v.getData() + " ");
//            qStr = qStr + v.getData();
            q.push(v);
            Log.d("Stack"+i, qStr);

        }
        Log.d("stack akhir : ", qStr);
        tv_stack.append("\nStack: " + qStr);
    }

    /* draws lines between each relating vertices */
    private class DrawView extends View {
        public DrawView(Context c) {
            super(c);
        }

        @Override
        public void onDraw(Canvas canvas) {
            if (root != null) {
                Queue<Vertex> q = new LinkedList<Vertex>();
                q.add(root);
                while (!q.isEmpty()) {
                    Vertex v = q.remove();
                    for (Vertex c : v.getChildren()) {
                        float startX = v.tv.getX() + v.tv.getWidth() / 2;
                        float startY = v.tv.getY() + v.tv.getHeight() / 2;
                        float stopX = c.tv.getX() + c.tv.getWidth() / 2;
                        float stopY = c.tv.getY() + c.tv.getHeight() / 2;

                        canvas.drawLine(startX, startY, stopX, stopY, paint);
                        q.add(c);
                    }
                }
            }
        }
    }

    public void removeVertex(View v) {
		/* TODO implement this */
        Toast.makeText(this, "To be implemented", Toast.LENGTH_LONG).show();
    }

}
