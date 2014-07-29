package br.com.rafael.pedidojpdroid;

import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TabHost.TabSpec;
import br.com.rafael.jpdroid.core.Jpdroid;
import br.com.rafael.jpdroid.exceptions.JpdroidException;
import br.com.rafael.pedidojpdroid.entity.Produto;

public class ProdutoActivity extends TabActivity {

	private static final int ADD_FOTO = 1;
	private Produto produto;
	private Jpdroid jpdroid;

	private EditText etNome;
	private EditText etQuantidade;
	private EditText etPreco;
	private static Spinner spUnidadeMedida;
	private ImageView ivFoto;
	private long _id = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_produto);

		jpdroid = Jpdroid.getInstance();

		//TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);

		TabSpec descritor = getTabHost().newTabSpec("tag1");
		descritor.setContent(R.id.produto);
		descritor.setIndicator("Produto", getResources().getDrawable(R.drawable.produto));
		getTabHost().addTab(descritor);

		descritor = getTabHost().newTabSpec("tag2");
		descritor.setContent(R.id.detalhes);
		descritor.setIndicator("Detalhes", getResources().getDrawable(R.drawable.detalhesproduto));
		getTabHost().addTab(descritor);

		descritor = getTabHost().newTabSpec("tag3");
		descritor.setContent(R.id.foto);
		descritor.setIndicator("Foto", getResources().getDrawable(R.drawable.fotoproduto));
		getTabHost().addTab(descritor);

		getTabHost().setCurrentTab(0);

		etNome = (EditText) findViewById(R.id.etNomeProduto);
		etQuantidade = (EditText) findViewById(R.id.etQtdProduto);
		etPreco = (EditText) findViewById(R.id.etPrecoProduto);
		spUnidadeMedida = (Spinner) findViewById(R.id.spUnidadeMedida);

		ivFoto = (ImageView) findViewById(R.id.ivFotoProduto);

		Intent i = getIntent();
		_id = i.getLongExtra("_id", 0);
		if (_id > 0) {
			produto = (Produto) jpdroid.retrieve(Produto.class, "_id = " + _id, true).get(0);
			if (produto.getFoto() != null) {
				ivFoto.setImageBitmap(produto.getFoto());
			}
			etNome.setText(produto.getNome());
			etQuantidade.setText(String.valueOf(produto.getQuantidade()));
			etPreco.setText(String.valueOf(produto.getPreco()));
			SelectSpinnerItemByValue(produto.getUnidadeMedida());
			
		} else {
			produto = new Produto();
		}
	}

	public void ClickSalvarProduto(View v) {

		try {
			produto.setNome(etNome.getText().toString());
			produto.setUnidadeMedida(spUnidadeMedida.getSelectedItem().toString());
			if(etQuantidade.getText() != null){
				produto.setQuantidade(Double.valueOf(etQuantidade.getText().toString()));
			}
			if(etPreco.getText() != null){
				produto.setPreco(Double.valueOf(etPreco.getText().toString()));
			}
			produto.setFoto(loadBitmapFromView(ivFoto));

			jpdroid.persist(produto);
			finish();

		} catch (JpdroidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@SuppressWarnings("unchecked")
  public static void SelectSpinnerItemByValue(String value) {
		ArrayAdapter<String> adapter = ((ArrayAdapter<String>) spUnidadeMedida.getAdapter());

		for (int position = 0; position < adapter.getCount(); position++) {
			if (adapter.getItem(position).toString().equals(value)) {
				spUnidadeMedida.setSelection(position);
				return;
			}
		}
	}

	public static Bitmap loadBitmapFromView(View v) {
		Bitmap b = Bitmap.createBitmap(v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
		v.draw(c);
		return b;
	}

	
	public void ClickAddFotoProduto(View v){
		Intent i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(i, ADD_FOTO);
	}
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == ADD_FOTO) {
			if (resultCode == RESULT_OK && data != null) {
				Bundle extras = data.getExtras();
				Bitmap bmp = (Bitmap) extras.get("data");
				ivFoto.setImageBitmap(bmp);
			}
		}

	}

}
