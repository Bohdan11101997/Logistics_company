package edu.netcracker.project.logistic.formatter;

import edu.netcracker.project.logistic.dao.RoleCrudDao;
import edu.netcracker.project.logistic.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
public class RoleFormatter implements Formatter<Role> {
    private RoleCrudDao roleDao;

    @Autowired
    public RoleFormatter(RoleCrudDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public Role parse(String text, Locale locale) throws ParseException {
        try {
            return roleDao.findOne(Long.parseLong(text))
                    .orElseThrow(() -> new ParseException("Invalid role id", 0));
        } catch (NumberFormatException ex) {
            throw new ParseException("Invalid role id", 0);
        }
    }

    @Override
    public String print(Role role, Locale locale) {
        String roleName = role.getRoleName().substring(5);
        String[] words = roleName.split("_");
        StringBuilder builder = new StringBuilder();
        for (String word: words) {
            builder.append(titleCase(word));
            builder.append(" ");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }

    private String titleCase(String input) {
        char[] letters = input.toCharArray();
        char[] result = new char[letters.length];
        for (int i = 0; i < letters.length; i++) {
            if (i == 0) {
                result[i] = Character.toUpperCase(letters[i]);
            } else {
                result[i] = Character.toLowerCase(letters[i]);
            }
        }
        return new String(result);
    }
}
