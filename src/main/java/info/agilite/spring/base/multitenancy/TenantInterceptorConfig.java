package info.agilite.spring.base.multitenancy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class TenantInterceptorConfig implements WebMvcConfigurer {
	@Value("${info.agilite.spring.base.multitenancy:#{false}}")
	private boolean utilizarMultiTenancy;
	
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    	if(utilizarMultiTenancy) {
    		registry.addInterceptor(createTenantInterceptor());
    	}
    }
    
    @Bean
    public TenantInterceptor createTenantInterceptor() {
    	return new TenantInterceptor();
    }
}