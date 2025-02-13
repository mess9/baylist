package org.baylist.util.convert;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class OracleBooleanType implements UserType<Boolean> {

	@Override
	public int getSqlType() {
		return Types.NUMERIC;
	}

	@Override
	public Class<Boolean> returnedClass() {
		return Boolean.class;
	}

	@Override
	public boolean equals(Boolean x, Boolean y) {
		if (x == y) return true;
		if (x == null || y == null) return false;
		return x.equals(y);
	}

	@Override
	public int hashCode(Boolean x) {
		return x == null ? 0 : x.hashCode();
	}

	@Override
	public Boolean nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
		Object value = rs.getObject(position);
		if (value == null) {
			return null;
		}
		return ((Number) value).intValue() == 1;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Boolean value, int index, SharedSessionContractImplementor session) throws SQLException {
		if (value == null) {
			st.setNull(index, Types.NUMERIC);
		} else {
			st.setInt(index, value ? 1 : 0);
		}
	}

	@Override
	public Boolean deepCopy(Boolean value) throws HibernateException {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Boolean value) throws HibernateException {
		return value;
	}

	@Override
	public Boolean assemble(Serializable cached, Object owner) throws HibernateException {
		return (Boolean) cached;
	}

	@Override
	public Boolean replace(Boolean original, Boolean target, Object owner) throws HibernateException {
		return original;
	}
}