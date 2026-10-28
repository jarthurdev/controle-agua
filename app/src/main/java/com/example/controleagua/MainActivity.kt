package com.example.controleagua

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.controleagua.databinding.ActivityMainBinding

/**
 * Tela Inicial do aplicativo ControleAgua.
 * Exibe a meta diária, quantidade consumida, quantidade restante e progresso visual.
 * Permite também a alteração da meta e navegação para as demais telas.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicialização do ViewBinding para acesso seguro aos componentes XML
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Instancia o auxiliar de persistência com SharedPreferences
        preferencesHelper = PreferencesHelper(this)

        configurarListeners()
    }

    override fun onResume() {
        super.onResume()
        // Atualiza a interface gráfica sempre que a tela volta a ficar visível
        atualizarDadosNaTela()
    }

    /**
     * Configura os eventos de clique dos botões da interface.
     */
    private fun configurarListeners() {
        // Botão para abrir o diálogo de configuração de nova meta
        binding.btnConfigurarMeta.setOnClickListener {
            exibirDialogoConfigurarMeta()
        }

        // Navegação para a Tela de Registro de Consumo
        binding.btnIrParaRegistro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        // Navegação para a Tela de Resumo do Dia
        binding.btnIrParaResumo.setOnClickListener {
            val intent = Intent(this, ResumoActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Carrega as informações salvas no SharedPreferences e atualiza as Views.
     */
    private fun atualizarDadosNaTela() {
        val meta = preferencesHelper.getMeta()
        val consumo = preferencesHelper.getConsumo()
        val restante = preferencesHelper.getRestante()
        val percentual = preferencesHelper.getPercentual()

        // Atualiza os textos informativos
        binding.tvMetaDiaria.text = getString(R.string.unit_ml, meta)
        binding.tvQuantidadeConsumida.text = getString(R.string.unit_ml, consumo)
        binding.tvQuantidadeRestante.text = getString(R.string.label_restante) + ": " + getString(R.string.unit_ml, restante)
        binding.tvPorcentagem.text = getString(R.string.percent_format, percentual)

        // Atualiza a barra de progresso visual (limitando a 100 para a barra física)
        val progressoBarra = percentual.coerceAtMost(100)
        binding.progressBarAgua.progress = progressoBarra
    }

    /**
     * Exibe um AlertDialog simples e prático para o usuário digitar a nova meta diária.
     */
    private fun exibirDialogoConfigurarMeta() {
        val input = EditText(this).apply {
            inputType = InputType.TYPE_CLASS_NUMBER
            hint = getString(R.string.hint_meta_ml)
            setText(preferencesHelper.getMeta().toString())
            setSelection(text.length)
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_meta_title)
            .setMessage(R.string.dialog_meta_message)
            .setView(input)
            .setPositiveButton(R.string.btn_salvar) { dialog, _ ->
                val texto = input.text.toString().trim()
                val novaMeta = texto.toIntOrNull()

                // Validação de entrada: impede valores nulos, vazios ou <= 0
                if (novaMeta != null && novaMeta > 0) {
                    preferencesHelper.salvarMeta(novaMeta)
                    atualizarDadosNaTela()
                    Toast.makeText(this, "Meta atualizada para $novaMeta ml!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, R.string.erro_valor_invalido, Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.btn_cancelar) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}
