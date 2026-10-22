package nttdata.bank.service.transacao;

import nttdata.bank.domain.entities.transacao.TipoDespesaEnum;
import nttdata.bank.service.GraficoStrategy;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

@Component
public class GraficoDespesaService implements GraficoStrategy {

    private static final Logger log = LoggerFactory.getLogger(GraficoDespesaService.class);

    private static final String TITULO_GRAFICO = "Distribuição de Despesas por Categoria";

    @Override
    public ByteArrayOutputStream gerarGrafico(Optional<?> dados) {
        log.info("Gerando gráfico de despesas...");
        Map<TipoDespesaEnum, BigDecimal> despesas = (Map<TipoDespesaEnum, BigDecimal>) dados.get();
        return createChart(createDataset(despesas));
    }

    private static PieDataset createDataset(Map<TipoDespesaEnum, BigDecimal> despesas) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        BigDecimal valorTotalDespesas = despesas.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        despesas.forEach((tipoDespesa, valor) -> {
            BigDecimal percentage = valor.divide(valorTotalDespesas, 2, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(100));

            dataset.setValue(tipoDespesa.getDescricao().concat(" (R$")
                    .concat(valor.toString()).concat(") (").concat(percentage.toString().concat("%)")), valor);
        });
        return dataset;
    }

    private static ByteArrayOutputStream  createChart(PieDataset dataset) {
        JFreeChart chart = ChartFactory.createPieChart(TITULO_GRAFICO, dataset, true, true, false);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(baos, chart, 800, 600);
            return baos;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar gráfico de despesas");
        }
    }
}
