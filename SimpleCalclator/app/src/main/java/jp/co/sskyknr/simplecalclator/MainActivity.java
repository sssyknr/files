package jp.co.sskyknr.simplecalclator;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    /** 自インスタンス・オブジェクト */
    private final MainActivity THIS = this;
    /** 計算結果表示 */
    private TextView mDiaplayResult;
    /** メモリ有無表示 */
    private TextView mDiaplayMemory;
    /** 計算式表示 */
    private TextView mDiaplayFormula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * Viewの初期化
     */
    private void init() {
        setContentView(R.layout.activity_main);

        mDiaplayResult = (TextView) findViewById(R.id.show_result_text);
        mDiaplayFormula = (TextView) findViewById(R.id.show_formula_text);
        mDiaplayMemory = (TextView) findViewById(R.id.show_memory_text);

        initNumberButton();
    }

    /**
     * 計算機ボタンの初期化
     */
    private void initNumberButton() {
        findViewById(R.id.number0).setOnClickListener(THIS);
        findViewById(R.id.number1).setOnClickListener(THIS);
        findViewById(R.id.number2).setOnClickListener(THIS);
        findViewById(R.id.number3).setOnClickListener(THIS);
        findViewById(R.id.number4).setOnClickListener(THIS);
        findViewById(R.id.number5).setOnClickListener(THIS);
        findViewById(R.id.number6).setOnClickListener(THIS);
        findViewById(R.id.number7).setOnClickListener(THIS);
        findViewById(R.id.number8).setOnClickListener(THIS);
        findViewById(R.id.number9).setOnClickListener(THIS);
        findViewById(R.id.number_dot).setOnClickListener(THIS);
        findViewById(R.id.number_equal).setOnClickListener(THIS);
        findViewById(R.id.number_dev).setOnClickListener(THIS);
        findViewById(R.id.number_minus).setOnClickListener(THIS);
        findViewById(R.id.number_multi).setOnClickListener(THIS);
        findViewById(R.id.number_plus).setOnClickListener(THIS);
    }
}
