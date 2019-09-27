package br.com.pedroenju.dao;

import br.com.pedroenju.contracts.ICriterion;
import br.com.pedroenju.contracts.IFilter;
import br.com.pedroenju.contracts.ISQLInsert;
import br.com.pedroenju.contracts.ISQLInstruction;
import br.com.pedroenju.contracts.ISQLUpdate;
import br.com.pedroenju.model.Estado;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Pedro Enju
 */
public class EstadoDao extends AbstractDao {

    public EstadoDao(Connection conn) {
        this.connect = conn;
    }

    @Override
    protected String getTableName() {
        return "estado";
    }

    @Override
    public void save(Object o) {
        Estado state = (Estado) o;

        ISQLInstruction sql = this.newInstruction(ISQLInstruction.QueryType.INSERT);
        if (state.getIdEstado() > 0) {
            sql = this.newInstruction(ISQLInstruction.QueryType.UPDATE);
        }

        ((ISQLUpdate) sql).addRowData("nomeEstado", state.getNomeEstado());
        ((ISQLUpdate) sql).addRowData("uf", state.getUf());
        if (sql instanceof ISQLUpdate) {
            ICriterion criterion = new ICriterion();
            criterion.addExpression(new IFilter("idEstado", "=", Long.toString(state.getIdEstado())));
            sql.setCriterion(criterion);
        } else if (sql instanceof ISQLInsert) {
            ((ISQLInsert) sql).getRowData().put("idEstado", null);
        }

        try {
            Object response = this.executeSQL(sql);
            if(sql instanceof ISQLInsert && response instanceof Long) {
                state.setIdEstado((Long) response);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public ArrayList<Object> getAll(ICriterion criterion) {
        ISQLInstruction sql = this.newInstruction(ISQLInstruction.QueryType.SELECT);
        sql.setCriterion(criterion);

        try {
            ArrayList<HashMap<String, Object>> list = this.executeSQL(sql);
            if (!list.isEmpty()) {
                ArrayList<Estado> states = new ArrayList();
                for (HashMap<String, Object> row : list) {
                    Estado state = new Estado();
                    state.setIdEstado((long) row.get("idEstado"));
                    state.setNomeEstado((String) row.get("nomeEstado"));
                    state.setUf((String) row.get("uf"));
                    states.add(state);
                }

                return (ArrayList) states;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    @Override
    public Object getById(Long id) {
        ICriterion criterion = new ICriterion();
        criterion.addExpression(new IFilter("idEstado", "=", Long.toString(id)));
        return this.getAll(criterion);
    }

    @Override
    public void delete(Object o) {
        Estado state = (Estado) o;
        
        if(state.getIdEstado() > 0) {
            ISQLInstruction sql = this.newInstruction(ISQLInstruction.QueryType.DELETE);
            ICriterion criterion = new ICriterion();
            criterion.addExpression(new IFilter("idEstado", "=", Long.toString(state.getIdEstado())));
            sql.setCriterion(criterion);
            
            try {
                this.executeSQL(sql);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

}