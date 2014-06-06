package br.com.rafael.pedidojpdroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ListaItensPedidoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lista_itens_pedido);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.lista_itens_pedido, menu);
		return true;
	}

}
