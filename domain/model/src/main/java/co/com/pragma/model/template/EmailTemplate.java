package co.com.pragma.model.template;

public enum EmailTemplate {
    STATE_CHANGE("state-change-notification"),
    PAY_PLAN("pay-plan-notification");

    private final String templateName;

    EmailTemplate(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateName() {
        return templateName;
    }
}