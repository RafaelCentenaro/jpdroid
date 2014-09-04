package com.rafael.androidcomjpdroid;

import java.io.Serializable;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import br.com.rafael.jpdroid.core.Jpdroid;

import com.rafael.androidcomjpdroid.entity.Contato;
import com.rafael.androidcomjpdroid.entity.TipoContato;

public class ContatoActivity extends Activity {

	Jpdroid database;
	private Contato contato = null;
	private EditText etContato;
	private static Spinner spTipo;
	private int posicao = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contato);
		
		database = Jpdroid.getInstance();
		
		etContato = (EditText) findViewById(R.id.etContato);
		spTipo = (Spinner) findViewById(R.id.spTipoContato);
		spTipo.setOnItemSelectedListener(evento);
		
		adquirirTipoContato();
		
		Intent it = getIntent();
		Serializable param = it.getExtras().getSerializable("contato");
		if (param != null) {
			posicao = it.getIntExtra("posicao", 0);
			Contato novo = (Contato) param;
			contato = novo;
			etContato.setText(contato.getContato());
			spTipo.setSelection((Long.valueOf(contato.getIdTipoContato()).intValue()-1));

		} else {
			contato = new Contato();
		}
	}

	private void adquirirTipoContato() {
		Cursor matrixCursor = database.createQuery(TipoContato.class);

		SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(
				this, android.R.layout.simple_list_item_2, matrixCursor,new String[]{"_id","descricao"}, 
				new int[]{android.R.id.text1,android.R.id.text2}, 0);

		spTipo.setAdapter(dataAdapter);
		
	}
	
	public void btnSalvarContatoOnClick(final View v) {

		contato.setContato(etContato.getText().toString());
		Cursor selectItem = (Cursor) spTipo.getSelectedItem();
		contato.setIdTipoContato(selectItem.getLong(0));
		contato.setNomeTipoContato(selectItem.getString(1));
		Intent it = new Intent();

		Bundle bundle = new Bundle();
		bundle.putSerializable("contato", (Serializable) contato);

		it.putExtras(bundle);
		it.putExtra("posicao", posicao);

		setResult(RESULT_OK, it);
		finish();
	}

	public void btnCancelarContatoOnClick(View v){
		Intent it = new Intent();
		setResult(RESULT_CANCELED, it);
		finish();
	}
	AdapterView.OnItemSelectedListener evento = new AdapterView.OnItemSelectedListener() {
		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
			if (spTipo.getSelectedItem().equals("Celular")) {
				etContato.setInputType(InputType.TYPE_CLASS_PHONE);
			} else if (spTipo.getSelectedItem().equals("E-mail")) {
				etContato.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			}
		}

		public void onNothingSelected(AdapterView<?> adapterView) {
			return;
		}
	};
}
