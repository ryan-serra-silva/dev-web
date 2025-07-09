
import com.mycompany.webapplication.entity.Investment;
import com.mycompany.webapplication.entity.InvestmentProduct;
import com.mycompany.webapplication.model.InvestmentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(name = "DetalhesInvestimento", urlPatterns = {"/DetalhesInvestimento"})
public class DetalhesInvestimento extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");

        if (idParam == null) {
            response.sendRedirect("Home");
            return;
        }

        try {
            long investimentoId = Long.parseLong(idParam);
            InvestmentDAO investmentDAO = new InvestmentDAO();
            Investment investimento = investmentDAO.getById(investimentoId);

            if (investimento == null) {
                request.setAttribute("mensagem", "Investimento não encontrado.");
                request.getRequestDispatcher("/views/investir.jsp").forward(request, response);
                return;
            }

            InvestmentProduct produto = investimento.getInvestmentProduct();
            long meses = java.time.temporal.ChronoUnit.MONTHS.between(
                investimento.getStartDate(), investimento.getEndDate()
            );

            BigDecimal taxaMensal = produto.getReturnRate();
            BigDecimal valorFinal = investimento.getAmount()
                .multiply((BigDecimal.ONE.add(taxaMensal)).pow((int) meses));

            request.setAttribute("investimento", investimento);
            request.setAttribute("produto", produto);
            request.setAttribute("tempo", meses);
            request.setAttribute("valorFinal", valorFinal.setScale(2, BigDecimal.ROUND_HALF_EVEN));
            request.getRequestDispatcher("/views/detalheInvestimento.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("Home");
        }
    }
}
