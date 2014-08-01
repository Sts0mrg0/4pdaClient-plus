package org.softeg.slartus.forpdaplus.controls.quickpost.items;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.softeg.slartus.forpdaplus.MyApp;
import org.softeg.slartus.forpdaplus.classes.AlertDialogBuilder;
import org.softeg.slartus.forpdaplus.classes.BbImage;
import org.softeg.slartus.forpdaplus.classes.common.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;

public class BbCodesQuickView extends BaseQuickView {

    public BbCodesQuickView(Context context) {
        super(context);
    }

    private int getSelectionStart(){
        return getEditor().getSelectionStart();
    }

    private int getSelectionEnd(){
        return getEditor().getSelectionEnd();
    }


    WebView webView;


    @Override
    View createView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        webView = new WebView(inflater.getContext());
        webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        loadWebView();
        return webView;
    }

    private void loadWebView() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body bgcolor=\"").append(MyApp.getInstance().getCurrentThemeName()).append("\">");
        String style = MyApp.getInstance().getCurrentThemeName();
        String path = "file:///android_asset/forum/style_images/1/folder_editor_buttons_" + style + "/";
        initVars();
        for (String key : m_BbCodes) {
            sb.append("<a style=\"text-decoration: none;\" href=\"")
                    .append(key).append("\">")
                    .append("<img style=\"padding:5px;\" src=\"")
                    .append(path).append(key.toLowerCase()).append(".png\" />").append("</a> ");
        }

        sb.append("</body></html>");
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadDataWithBaseURL("http://4pda.ru/forum/", sb.toString(), "text/html", "UTF-8", null);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                    getSelectionStart() = getEditor().getSelectionStart();
//                    getSelectionEnd() = getEditor().getSelectionEnd();
                }
                return false;
            }
        });
    }

    public class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(android.webkit.WebView view, java.lang.String url) {
            Uri uri = Uri.parse(url);
            if (uri.getPathSegments() == null || uri.getPathSegments().size() < 2) return true;
            String bbCode = uri.getPathSegments().get(1);
            int i = ArrayUtils.indexOf(bbCode, m_BbCodes);
            try {

                switch (bbCode) {
                    case "LIST":
                        getListBbCodeOnClickListener("");
                        break;
                    case "NUMLIST":
                        getListBbCodeOnClickListener("=1");
                        break;
                    case "URL":
                        getUrlBbCodeOnClickListener();
                        break;
                    case "SPOILER":
                        getSpoilerBbCodeOnClickListener(i);
                        break;
                    case "COLOR":
                    case "BACKGROUND":
                        getColorBbCodeOnClickListener(bbCode, i);
                        break;
                    case "SIZE":
                        getSizeBbCodeOnClickListener(i);
                        break;
                    default:

                        bbCodeClick(i);

                        break;
                }
            } catch (Exception ex) {
                org.softeg.slartus.forpdaplus.common.Log.e(getContext(), ex);
            }

            return true;
        }
    }

    private void getSizeBbCodeOnClickListener(int tagIndex) {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        if (selectionEnd < selectionStart && selectionEnd != -1) {
            int c = selectionStart;
            selectionStart = selectionEnd;
            selectionEnd = c;
        }

        if (selectionStart == -1)
            selectionStart = 0;
        if (selectionEnd == -1)
            selectionEnd = 0;

        final int finalSelectionStart = selectionStart;
        final int finalSelectionEnd = selectionEnd;
        CharSequence[] items = new CharSequence[]{"1", "2", "3", "4", "5", "6", "7"};
        new AlertDialogBuilder(getContext())
                .setTitle("Выберите размер текста")
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        String tag = "[SIZE=" + Integer.toString(i + 1) + "]";
                        getEditor().getText().insert(finalSelectionStart, tag);
                        getEditor().getText().insert(finalSelectionEnd + tag.length(), "[/SIZE]");
                    }
                })
                .setCancelable(true)
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();

    }

    private class BBColor {
        public String ColorName;
        public String ColorValue;

        public BBColor(String colorName, String colorValue) {
            ColorName = colorName;
            ColorValue = colorValue;
        }
    }

    private Dialog m_ColorsDialog;


    private void showColorDialog(final int selectionStart, final int selectionEnd, final String bbCode) {
        ArrayList<BBColor> colors = new ArrayList<BBColor>();
        colors.add(new BBColor("black", "#000000"));
        colors.add(new BBColor("white", "#FFFFFF"));
        colors.add(new BBColor("skyblue", "#82CEE8"));
        colors.add(new BBColor("royalblue", "#426AE6"));
        colors.add(new BBColor("blue", "#0000FF"));
        colors.add(new BBColor("darkblue", "#07008C"));
        colors.add(new BBColor("orange", "#FDA500"));
        colors.add(new BBColor("orangered", "#FF4300"));
        colors.add(new BBColor("crimson", "#E1133A"));
        colors.add(new BBColor("red", "#FF0000"));
        colors.add(new BBColor("darkred", "#8C0000"));
        colors.add(new BBColor("green", "#008000"));
        colors.add(new BBColor("limegreen", "#41A317"));
        colors.add(new BBColor("seagreen", "#4E8975"));
        colors.add(new BBColor("deeppink", "#F52887"));
        colors.add(new BBColor("tomato", "#FF6245"));
        colors.add(new BBColor("coral", "#F76541"));
        colors.add(new BBColor("purple", "#800080"));
        colors.add(new BBColor("indigo", "#440087"));
        colors.add(new BBColor("burlywood", "#E3B382"));
        colors.add(new BBColor("sandybrown", "#EE9A4D"));
        colors.add(new BBColor("sienna", "#C35817"));
        colors.add(new BBColor("chocolate", "#C85A17"));
        colors.add(new BBColor("teal", "#037F81"));
        colors.add(new BBColor("silver", "#C0C0C0"));

        ScrollView scrollView = new ScrollView(getContext());

        final LinearLayout tl = new LinearLayout(getContext());
        tl.setOrientation(LinearLayout.VERTICAL);

        int defaultRowCount = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 5 : 3;
        int rowCount = defaultRowCount;

        LinearLayout.LayoutParams imgLayoutParams
                = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        imgLayoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                (float) 45, getContext().getResources().getDisplayMetrics());
        imgLayoutParams.setMargins(2, 2, 2, 2);
        LinearLayout tr = null;

        for (final BBColor bbColor : colors) {
            if (rowCount == defaultRowCount) {
                tr = new LinearLayout(getContext());
                tr.setOrientation(LinearLayout.HORIZONTAL);
                tl.addView(tr);
                rowCount = 0;
            }
            final String key = bbColor.ColorName;
            final String colorValue = bbColor.ColorValue;

            Button imageButton = new Button(getContext());
            imageButton.setBackgroundColor(Color.parseColor(colorValue));
            if (key.equals("white"))
                imageButton.setTextColor(Color.BLACK);
            else
                imageButton.setTextColor(Color.WHITE);
            imageButton.setContentDescription(key);
            imageButton.setText(key);
            imageButton.setTag("colored");
            imageButton.setLayoutParams(imgLayoutParams);
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    m_ColorsDialog.dismiss();
                    m_ColorsDialog = null;
                    String tag = "[" + bbCode + "=" + key + "]";
                    getEditor().getText().insert(selectionStart, tag);
                    getEditor().getText().insert(selectionEnd + tag.length(), "[/" + bbCode + "]");
                }
            });

            tr.addView(imageButton, imgLayoutParams);

            rowCount++;
        }


        scrollView.addView(tl, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        m_ColorsDialog = new AlertDialogBuilder(getContext())
                .setCancelable(true)
                .setView(scrollView)
                .create();
        m_ColorsDialog.show();
    }

    private void getColorBbCodeOnClickListener(final String bbCode, int tagIndex) {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        if (selectionEnd < selectionStart && selectionEnd != -1) {
            int c = selectionStart;
            selectionStart = selectionEnd;
            selectionEnd = c;
        }

        if (selectionStart == -1)
            selectionStart = 0;
        if (selectionEnd == -1)
            selectionEnd = 0;

        int mColor = 0xFFFF0000;
        final int finalSelectionStart = selectionStart;
        final int finalSelectionEnd = selectionEnd;
        showColorDialog(selectionStart, selectionEnd, bbCode);
//        new UberColorPickerDialog(mContext, new UberColorPickerDialog.OnColorChangedListener() {
//            @Override
//            public void colorChanged(int color) {
//
//                String tag = "[" + bbCode + "=" + ExtColor.encodeRGB(color) + "]";
//                txtPost.getText().insert(finalSelectionStart, tag);
//                txtPost.getText().insert(finalSelectionEnd + tag.length(), "[/" + bbCode + "]");
//            }
//
//            @Override
//            public void canceled() {
//
//            }
//        }, mColor, false).show();
    }


    private void getSpoilerBbCodeOnClickListener(int tagIndex) {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        if (selectionEnd < selectionStart && selectionEnd != -1) {
            int c = selectionStart;
            selectionStart = selectionEnd;
            selectionEnd = c;
        }
        String spoilerText = null;
        if (selectionStart != -1 && selectionStart != selectionEnd) {
            spoilerText = getEditor().getText().toString()
                    .substring(selectionStart, selectionEnd);
        } else {
            if (mNotClosedCodes[tagIndex] > 0) {
                getEditor().getText().insert(selectionStart, "[/SPOILER]");
                mNotClosedCodes[tagIndex]--;
                return;
            }
        }
        createSpoilerDialog(spoilerText, tagIndex);
    }

    private void createSpoilerDialog(final String spoilerText, final int tagIndex) {


        LinearLayout layout = new LinearLayout(getContext());

        layout.setPadding(5, 5, 5, 5);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView tx = new TextView(getContext());
        tx.setText("Название спойлера");
        layout.addView(tx);

        // Set an EditText view to get user input
        final EditText input = new EditText(getContext());

        input.requestFocus();
        layout.addView(input);
        final int[] selectionStart = {getSelectionStart()};
        final int[] selectionEnd = {getSelectionEnd()};

        AlertDialog alertDialog = new AlertDialogBuilder(getContext())
                .setView(layout)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();


                        if (selectionEnd[0] < selectionStart[0] && selectionEnd[0] != -1) {
                            int c = selectionStart[0];
                            selectionStart[0] = selectionEnd[0];
                            selectionEnd[0] = c;
                        }
                        String spoilerName = input.getText() == null ? "" : input.getText().toString();
                        if (!TextUtils.isEmpty(spoilerName))
                            spoilerName = "=" + spoilerName;
                        String bbcode = "SPOILER";//TextUtils.isEmpty(spoilerName) ? "SPOILER" : "SPOIL";
                        String startSpoiler = "[" + bbcode + spoilerName + "]";

                        if (getEditor().getText() != null) {
                            if (selectionStart[0] != -1 && selectionStart[0] != selectionEnd[0])
                                getEditor().getText().replace(selectionStart[0], selectionEnd[0], startSpoiler + spoilerText + "[/" + bbcode + "]");
                            else {
                                getEditor().getText().insert(selectionStart[0], startSpoiler);
                                mNotClosedCodes[tagIndex]++;
                            }
                        }
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                }).create();
        
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                public void onShow(DialogInterface dialogInterface) {
                    input.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(input, 0);
                }
            });
        
        alertDialog.show();
    }


    protected void initVars() {
        m_BbCodes = new String[]{"B", "I", "U", "S", "SUB", "SUP", "LEFT", "CENTER",
                "RIGHT", "URL", "QUOTE", "OFFTOP", "CODE", "SPOILER", "HIDE", "LIST", "NUMLIST", "COLOR", "BACKGROUND",
                "SIZE", "CUR"};
        mNotClosedCodes = new int[m_BbCodes.length];  //+1 - для спойлера с отрицательным индексом
    }

    protected BbImage[] getImages() {
        String style = MyApp.getInstance().getCurrentThemeName();

        BbImage[] res = new BbImage[m_BbCodes.length];
        String path = "file:///android_asset/forum/style_images/1/folder_editor_buttons_" + style + "/";
        for (int i = 0; i < res.length; i++) {

            res[i] = new BbImage(path, m_BbCodes[i].toLowerCase() + ".png", m_BbCodes[i]);
        }
        return res;
    }

    private String[] m_BbCodes;
    private int[] mNotClosedCodes;


    private void getUrlBbCodeOnClickListener() {

        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        if (selectionEnd < selectionStart && selectionEnd != -1) {
            int c = selectionStart;
            selectionStart = selectionEnd;
            selectionEnd = c;
        }
        String urlText = null;
        if (selectionStart != -1 && selectionStart != selectionEnd) {
            urlText = getEditor().getText().toString()
                    .substring(selectionStart, selectionEnd);
        }

        createUrlDialog(null, urlText, "Пожалуйста, введите полный URL адрес", "");

    }

    private void createUrlDialog(final String url, final String urlText, String captionText, String editText) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setPadding(5, 5, 5, 5);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView tx = new TextView(getContext());
        tx.setText(captionText);
        layout.addView(tx);

        // Set an EditText view to get user input
        final EditText input = new EditText(getContext());
        input.setText(editText);
        input.requestFocus();
        layout.addView(input);
        final int[] selectionStart = {getSelectionStart()};
        final int[] selectionEnd = {getSelectionEnd()};
        AlertDialog alertDialog = new AlertDialogBuilder(getContext())
                .setCancelable(false)
                .setView(layout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        String tempUrlText = urlText;
                        String tempUrl = url;
                        if (!TextUtils.isEmpty(url)) {
                            tempUrlText = input.getText().toString();
                        } else {
                            tempUrl = input.getText().toString();
                        }

                        if (TextUtils.isEmpty(tempUrlText)) {
                            createUrlDialog(input.getText().toString(), null, "Пожалуйста, введите заголовок", "");
                            return;
                        }

                        if (selectionEnd[0] < selectionStart[0] && selectionEnd[0] != -1) {
                            int c = selectionStart[0];
                            selectionStart[0] = selectionEnd[0];
                            selectionEnd[0] = c;
                        }
                        getEditor().getText().replace(selectionStart[0], selectionEnd[0], "[URL=" + (tempUrl == null ? "" : tempUrl) + "]" + tempUrlText + "[/URL]");
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create();


        alertDialog.show();
    }

    private void getListBbCodeOnClickListener(final String listTagPostFix) throws IOException {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        if (selectionEnd < selectionStart && selectionEnd != -1) {
            int c = selectionStart;
            selectionStart = selectionEnd;
            selectionEnd = c;
        }
        if (selectionStart != -1 && selectionStart != selectionEnd) {
            String selectedText = getEditor().getText().toString()
                    .substring(selectionStart, selectionEnd);
            while (selectedText.indexOf("\n\n") != -1) {
                selectedText = selectedText.replace("\n\n", "\n");
            }
            String modifiedText = "[LIST" + listTagPostFix + "]"
                    + selectedText
                    .replaceAll("^", "[*]")
                    .replace("\n", "\n[*]")
                    + "[/LIST]";
            getEditor().getText().replace(selectionStart, selectionEnd, modifiedText);
            return;
        }
        StringBuilder sb = new StringBuilder();
        createListDialog(1, sb, listTagPostFix);

    }

    private void createListDialog(final int ind, final StringBuilder sb, final String listTagPostFix) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setPadding(5, 5, 5, 5);
        layout.setOrientation(LinearLayout.VERTICAL);

        final TextView tx = new TextView(getContext());
        tx.setText("Введите содержание " + ind + " пункта списка");
        layout.addView(tx);

        // Set an EditText view to get user input
        final EditText input = new EditText(getContext());
        input.requestFocus();
        layout.addView(input);

        AlertDialog alertDialog = new AlertDialogBuilder(getContext())
                .setCancelable(false)
                .setView(layout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (TextUtils.isEmpty(input.getText().toString())) {
                            tryInsertListText(sb, listTagPostFix);
                            return;
                        }
                        sb.append("[*]").append(input.getText().toString()).append("\n");
                        createListDialog(ind + 1, sb, listTagPostFix);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        tryInsertListText(sb, listTagPostFix);

                    }
                }).create();
        
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                public void onShow(DialogInterface dialogInterface) {
                    input.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(input, 0);
                }
            });
        
        alertDialog.show();
    }

    private void tryInsertListText(StringBuilder sb, final String listTagPostFix) {
        String text = sb.toString().trim();
        if (TextUtils.isEmpty(text)) return;

        int selectionStart = getSelectionStart();
        getEditor().getText().insert(selectionStart, "[LIST" + listTagPostFix + "]" + text + "[/LIST]");
    }

    private void bbCodeClick(int tagIndex) {
        String tag = m_BbCodes[tagIndex];

        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        if (selectionEnd < selectionStart && selectionEnd != -1) {
            int c = selectionStart;
            selectionStart = selectionEnd;
            selectionEnd = c;
        }
        if (selectionStart != -1 && selectionStart != selectionEnd) {
            getEditor().getText().insert(selectionStart, "[" + tag + "]");
            getEditor().getText().insert(selectionEnd + tag.length() + 2, "[/" + tag + "]");
            return;
        }

        if (mNotClosedCodes[tagIndex] > 0) {
            getEditor().getText().insert(selectionStart, "[/" + tag + "]");
            mNotClosedCodes[tagIndex]--;
        } else {
            getEditor().getText().insert(selectionStart, "[" + tag + "]");
            mNotClosedCodes[tagIndex]++;
        }
    }


}