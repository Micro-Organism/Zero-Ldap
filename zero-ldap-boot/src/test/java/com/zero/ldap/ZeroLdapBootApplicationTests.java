package com.zero.ldap;

import com.zero.ldap.domain.entity.SystemUserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ldap.NamingException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@SpringBootTest
class ZeroLdapBootApplicationTests {

    @Autowired
    private LdapTemplate ldapTemplate;

    /**
     * add person
     */
    @Test
    public void addPerson() {
        SystemUserEntity person = new SystemUserEntity();
        person.setUid("uid:14");
        person.setSuerName("LISI");
        person.setCommonName("lisi");
        person.setUserPassword("123456");
        ldapTemplate.create(person);
    }

    /**
     * filter search
     */
    @Test
    public void filterSearch() {
        // Get the domain list. If you want to get a certain domain, the filter can be written like this: (&(objectclass=dcObject)&(dc=example))
        // String filter = "(&(objectclass=dcObject))";
        // Get the list of organizations. If you want to get a specific organization, the filter can be written like this: (&(objectclass=organizationalUnit)&(ou=people)
        // String filter = "(&(objectclass=organizationalUnit))";
        //Get the people list. If you want to get a certain person, the filter can be written like this: (&(objectclass=inetOrgPerson)&(uid=uid:13))
        String filter = "(&(objectclass=inetOrgPerson))";
        List<SystemUserEntity> list = ldapTemplate.search("", filter, new AttributesMapper() {
            @Override
            public Object mapFromAttributes(Attributes attributes) throws NamingException, javax.naming.NamingException {
                //如果不知道ldap中有哪些属性，可以使用下面这种方式打印
                NamingEnumeration<? extends Attribute> att = attributes.getAll();
                while (att.hasMore()) {
                    Attribute a = att.next();
                    System.out.println(a.getID() + "=" + a.get());
                }

                SystemUserEntity p = new SystemUserEntity();

                Attribute a = attributes.get("cn");
                if (a != null) p.setCommonName((String) a.get());

                a = attributes.get("uid");
                if (a != null) p.setUid((String) a.get());

                a = attributes.get("sn");
                if (a != null) p.setSuerName((String) a.get());

                a = attributes.get("userPassword");
                if (a != null) p.setUserPassword(a.get().toString());
                return p;
            }
        });

        list.stream().forEach(System.out::println);
    }

    /**
     * query search
     */
    @Test
    public void querySearch() {
        // You can also use filter query method, filter is (&(objectClass=user)(!(objectClass=computer))
        List<SystemUserEntity> personList = ldapTemplate.search(query()
                        .where("objectClass").is("inetOrgPerson")
                        .and("uid").is("uid:14"),
                new AttributesMapper() {
                    @Override
                    public SystemUserEntity mapFromAttributes(Attributes attributes) throws NamingException, javax.naming.NamingException {
                        //If you don’t know what attributes are in ldap, you can print them in the following way
                        // NamingEnumeration<? extends Attribute> att = attr.getAll();
                        //while (att.hasMore()) {
                        //  Attribute a = att.next();
                        // System.out.println(a.getID());
                        //}
                        SystemUserEntity p = new SystemUserEntity();

                        Attribute a = attributes.get("cn");
                        if (a != null) p.setCommonName((String) a.get());

                        a = attributes.get("uid");
                        if (a != null) p.setUid((String) a.get());

                        a = attributes.get("sn");
                        if (a != null) p.setSuerName((String) a.get());

                        a = attributes.get("userPassword");
                        if (a != null) p.setUserPassword(a.get().toString());
                        return p;
                    }
                });
        personList.stream().forEach(System.out::println);
    }

}
