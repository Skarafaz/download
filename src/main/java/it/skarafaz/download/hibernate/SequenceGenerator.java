package it.skarafaz.download.hibernate;

import java.util.Properties;

import org.hibernate.boot.model.relational.QualifiedName;
import org.hibernate.boot.model.relational.QualifiedNameParser;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

public class SequenceGenerator extends SequenceStyleGenerator {
    private static final String SEQUENCE_POSTFIX_NAME = "_id_seq";

    @Override
    protected QualifiedName determineSequenceName(Properties params, Dialect dialect, JdbcEnvironment jdbcEnv) {
        String tableName = params.getProperty(PersistentIdentifierGenerator.TABLE);
        return QualifiedNameParser.INSTANCE.parse(tableName + SEQUENCE_POSTFIX_NAME);
    }
}
