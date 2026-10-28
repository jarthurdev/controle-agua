package com.example.controleagua

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.controleagua.databinding.ActivityResumoBinding

/**
 * Tela de Resumo Diário.
 * Apresenta métricas consolidadas do dia: total consumido, meta estipulada,
 * percentual de conclusão e mensagem de status motivacional.
 */
class ResumoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResumoBinding
    private lateinit var preferencesHelper: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResumoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesHelper = PreferencesHelper(this)

        configurarListeners()
    }

    override fun onResume() {
        super.onResume()
        carregarResumo()
    }

    /**
     * Configura ações dos botões da interface.
     */
    private fun configurarListeners() {
        binding.btnVoltarResumoTopo.setOnClickListener {
            finish()
        }

        binding.btnVoltarResumo.setOnClickListener {
            finish()
        }

        // Permite ir direto para a tela de registro de água a partir do resumo
        binding.btnIrParaRegistroResumo.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Lê os dados atuais do SharedPreferences e preenche a interface do resumo.
     */
    private fun carregarResumo() {
        val meta = preferencesHelper.getMeta()
        val consumo = preferencesHelper.getConsumo()
        val restante = preferencesHelper.getRestante()
        val percentual = preferencesHelper.getPercentual()
        val dataFormatada = preferencesHelper.obterDataFormatadaExibicao()

        // Exibe a data de hoje
        binding.tvDataHoje.text = getString(R.string.label_data_hoje, dataFormatada)

        // Exibe dados numéricos
        binding.tvResumoConsumo.text = getString(R.string.unit_ml, consumo)
        binding.tvResumoMeta.text = getString(R.string.unit_ml, meta)
        binding.tvResumoPercentual.text = getString(R.string.percent_format, percentual)

        // Atualiza a barra de progresso visual
        binding.progressBarResumo.progress = percentual.coerceAtMost(100)

        // Define status motivacional com base no progresso
        when {
            consumo >= meta && meta > 0 -> {
                binding.tvStatusTitulo.text = "Meta Concluída!"
                if (consumo > meta) {
                    binding.tvStatusMensagem.text = getString(R.string.status_meta_superada)
                } else {
                    binding.tvStatusMensagem.text = getString(R.string.status_meta_atingida)
                }
                binding.tvStatusTitulo.setTextColor(ContextCompat.getColor(this, R.color.success))
            }
            else -> {
                binding.tvStatusTitulo.text = "Meta em Andamento"
                binding.tvStatusMensagem.text = getString(R.string.status_meta_pendente, restante)
                binding.tvStatusTitulo.setTextColor(ContextCompat.getColor(this, R.color.primary))
            }
        }
    }
}
