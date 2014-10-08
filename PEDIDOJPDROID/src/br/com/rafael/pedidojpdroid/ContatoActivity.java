package br.com.rafael.pedidojpdroid;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import br.com.rafael.pedidojpdroid.entity.Contato;

public class ContatoActivity extends Activity {

	private Contato contato = null;
	private EditText etContato;
	private static Spinner spTipo;
	private int posicao = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contato);

		etContato = (EditText) findViewById(R.id.etContato1);
		spTipo = (Spinner) findViewById(R.id.spTipo);

		spTipo.setOnItemSelectedListener(evento);

		Intent it = getIntent();
		Serializable param = it.getExtras().getSerializable("contato");
		if (param != null) {
			posicao = it.getIntExtra("posicao", 0);
			Contato novo = (Contato) param;
			contato = novo;
			etContato.setText(contato.getContato());
			SelectSpinnerItemByValue(contato.getTipo());

		} else {
			contato = new Contato();
		}

	}

	@SuppressWarnings("unchecked")
  public static void SelectSpinnerItemByValue(String value) {
		ArrayAdapter<String> adapter = ((ArrayAdapter<String>) spTipo.getAdapter());

		for (int position = 0; position < adapter.getCount(); position++) {
			if (adapter.getItem(position).toString().equals(value)) {
				spTipo.setSelection(position);
				return;
			}
		}
	}

	AdapterView.OnItemSelectedListener evento = new AdapterView.OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
			if (spTipo.getSelectedItem().equals("Celular")) {
				etContato.setInputType(InputType.TYPE_CLASS_PHONE);
			} else if (spTipo.getSelectedItem().equals("Email")) {
				etContato.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			}
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
			return;
		}
	};

	public void btnSalvarContatoClicked(final View v) {

		contato.setContato(etContato.getText().toString());
		contato.setTipo(spTipo.getSelectedItem().toString());
		Intent it = new Intent();

		Bundle bundle = new Bundle();
		bundle.putSerializable("contato", (Serializable) contato);

		it.putExtras(bundle);
		it.putExtra("posicao", posicao);

		setResult(RESULT_OK, it);
		finish();
	}

}
