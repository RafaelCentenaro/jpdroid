package br.com.rafael.pedidojpdroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import br.com.rafael.jpdroid.core.Jpdroid;
import br.com.rafael.jpdroid.enums.ScriptPath;
import br.com.rafael.pedidojpdroid.entity.Cidade;
import br.com.rafael.pedidojpdroid.entity.Contato;
import br.com.rafael.pedidojpdroid.entity.Endereco;
import br.com.rafael.pedidojpdroid.entity.Estado;
import br.com.rafael.pedidojpdroid.entity.ItensPedido;
import br.com.rafael.pedidojpdroid.entity.Pedido;
import br.com.rafael.pedidojpdroid.entity.Pessoa;
import br.com.rafael.pedidojpdroid.entity.Produto;

public class PrincipalActivity extends Activity {

	private Jpdroid dataBase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_principal);

		dataBase = Jpdroid.getInstance();
		dataBase.setContext(this);
		
		dataBase.addEntity(Estado.class);
		dataBase.addEntity(Cidade.class);
		dataBase.addEntity(Pessoa.class);
		dataBase.addEntity(Endereco.class);
		dataBase.addEntity(Contato.class);
		dataBase.addEntity(Produto.class);
		dataBase.addEntity(Pedido.class);
		dataBase.addEntity(ItensPedido.class);
		
		dataBase.open();
		
 		if(dataBase.isCreate()){
 			dataBase.importSqlScript(ScriptPath.Assets, "import.sql");
		}

	}

	public void onClickCadPessoa(View v){
		Intent i = new Intent(this,ListagemPessoaActivity.class);
		startActivity(i);
		
	}
	public void onClickCadProduto(View v){
		Intent i = new Intent(this,ListagemProdutoActivity.class);
		startActivity(i);
		
	}
	
	public void onClickExportacoes(View v){
		Intent i = new Intent(this,ExportacoesActivity.class);
		startActivity(i);
		
	}
	public void onClickImportacao(View v){
		Intent i = new Intent(this,ImportacoesActivity.class);
		startActivity(i);
		
	}
	public void onClickCadPedido(View v){
		Intent i = new Intent(this,ListagemPedidoActivity.class);
		startActivity(i);
	}
	public void onClickSobre(View v){
		Intent i = new Intent(this,SobreActivity.class);
		startActivity(i);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.principal, menu);
		return true;
	}

}
