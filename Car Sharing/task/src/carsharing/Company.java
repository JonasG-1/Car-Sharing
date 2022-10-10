package carsharing;

import java.util.Optional;

public class Company {

    private final String companyName;
    private final Integer companyId;

    public Company(String companyName) {
        this.companyName = companyName;
        companyId = null;
    }

    public Company(String companyName, int companyId) {
        this.companyName = companyName;
        this.companyId = companyId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Optional<Integer> getCompanyId() {
        return Optional.ofNullable(companyId);
    }
}
