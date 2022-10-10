package carsharing;

import java.util.List;
import java.util.Optional;

public interface CompanyDao {

    void addCompany(Company company);

    List<Company> getCompanies();

    Optional<Company> getCompanyById(int id);
}
