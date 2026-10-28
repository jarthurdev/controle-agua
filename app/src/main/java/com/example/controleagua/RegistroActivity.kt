package com.example.controleagua

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controleagua.databinding.ActivityRegistroBinding

/**
 * Tela de Registro de Consumo.
 * Permite ao usuário adicionar água rapidamente (+200 ml, +300 ml)
 * ou informar um valor personalizado em ml com validação de dados.
 */
class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesHelper = PreferencesHelper(this)

        configurarAcoes()
    }

    /**
     * Define os listeners para os botões de atalho, campo personalizado e retorno.
     */
    private fun configurarAcoes() {
        // Atalho rápido: +200 ml
        binding.btnAdd200.setOnClickListener {
            registrarAgua(200)
        }

        // Atalho rápido: +300 ml
        binding.btnAdd300.setOnClickListener {
            registrarAgua(300)
        }

        // Adicionar quantidade personalizada
        binding.btnAdicionarPersonalizado.setOnClickListener {
            processarQuantidadePersonalizada()
        }

        // Botões para retornar à tela inicial
        binding.btnVoltarTopo.setOnClickListener {
            finish()
        }

        binding.btnVoltarPrincipal.setOnClickListener {
            finish()
        }
    }

    /**
     * Valida o valor digitado no campo de texto e realiza a persistência.
     */
    private fun processarQuantidadePersonalizada() {
        val textoDigitado = binding.etQuantidadePersonalizada.text?.toString()?.trim()

        // Validação 1: Campo vazio
        if (textoDigitado.isNullOrEmpty()) {
            binding.tilQuantidadePersonalizada.error = getString(R.string.erro_campo_vazio)
            return
        }

        val quantidade = textoDigitado.toIntOrNull()

        // Validação 2: Valor menor ou igual a zero ou formato inválido
        if (quantidade == null || quantidade <= 0) {
            binding.tilQuantidadePersonalizada.error = getString(R.string.erro_valor_invalido)
            return
        }

        // Limpa indicação de erro se os dados forem válidos
        binding.tilQuantidadePersonalizada.error = null

        // Registra o consumo
        registrarAgua(quantidade)

        // Limpa o campo de entrada para o próximo registro
        binding.etQuantidadePersonalizada.text?.clear()
    }

    /**
     * Salva o consumo no SharedPreferences e emite um Toast informativo.
     */
    private fun registrarAgua(quantidade: Int) {
        preferencesHelper.adicionarConsumo(quantidade)
        val mensagem = getString(R.string.msg_sucesso_registro, quantidade)
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
    }
}
