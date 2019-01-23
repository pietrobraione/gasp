package gasp.ga.jbse;

import jbse.rewr.CalculatorRewriting;
import jbse.rewr.Rewriter;
import jbse.val.Any;
import jbse.val.HistoryPoint;
import jbse.val.Operator;
import jbse.val.Primitive;
import jbse.val.Simplex;
import jbse.val.Term;
import jbse.val.Value;
import jbse.val.exc.InvalidOperandException;
import jbse.val.exc.InvalidOperatorException;
import jbse.val.exc.InvalidTypeException;

public final class CalculatorRewritingSynchronized extends CalculatorRewriting {
	@Override
	public synchronized void addRewriter(Rewriter r) {
		super.addRewriter(r);
	}
	
	@Override
	public synchronized Any valAny() {
        return super.valAny();
	}
	
	@Override
	public synchronized Simplex valBoolean(boolean value) {
		return super.valBoolean(value);
	}
	
	@Override
	public synchronized Simplex valByte(byte value) {
		return super.valByte(value);
	}
	
	@Override
	public synchronized Simplex valShort(short value) {
		return super.valShort(value);
	}
	
	@Override
	public synchronized Simplex valInt(int value) {
		return super.valInt(value);
	}
	
	@Override
	public synchronized Simplex valLong(long value) {
		return super.valLong(value);
	}

	@Override
	public synchronized Simplex valFloat(float value) {
		return super.valFloat(value);
	}
	
	@Override
	public synchronized Simplex valDouble(double value) {
		return super.valDouble(value);
	}
	
	@Override
	public synchronized Simplex valChar(char value) {
		return super.valChar(value);
	}
	
	@Override
	public synchronized Term valTerm(char type, String value) throws InvalidTypeException {
		return super.valTerm(type, value);
	}
	
	@Override
	public synchronized Simplex val_(double val) {
		return super.val_(val);
	}
	
	@Override
	public synchronized Simplex val_(float val) {
		return super.val_(val);
	}
	
	@Override
	public synchronized Simplex val_(long val) {
		return super.val_(val);
	}
	
	@Override
	public synchronized Simplex val_(int val) {
		return super.val_(val);
	}
	
	@Override
	public synchronized Simplex val_(short val) {
		return super.val_(val);
	}
	
	@Override
	public synchronized Simplex val_(byte val) {
		return super.val_(val);
	}
	
	@Override
	public synchronized Simplex val_(char val) {
		return super.val_(val);
	}
	
	@Override
	public synchronized Simplex val_(boolean val) {
		return super.val_(val);
	}
	
	@Override
	public synchronized Simplex val_(Object v) {
		return super.val_(v);
	}
	
	@Override
	public synchronized Value createDefault(char type) {
		return super.createDefault(type);
	}
	
	@Override
	public synchronized Primitive add(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.add(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive mul(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.mul(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive sub(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.sub(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive div(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.div(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive rem(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.rem(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive neg(Primitive operand) throws InvalidOperandException, InvalidTypeException {
		return super.neg(operand);
	}

	@Override
	public synchronized Primitive andBitwise(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.andBitwise(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive orBitwise(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.orBitwise(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive xorBitwise(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.xorBitwise(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive and(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.and(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive or(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.or(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive not(Primitive operand) throws InvalidOperandException, InvalidTypeException {
		return super.not(operand);
	}

	@Override
	public synchronized Primitive shl(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.shl(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive shr(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.shr(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive ushr(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.ushr(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive eq(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.eq(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive ne(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.ne(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive le(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.le(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive lt(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.lt(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive ge(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.ge(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive gt(Primitive firstOperand, Primitive secondOperand)
			throws InvalidOperandException, InvalidTypeException {
		return super.gt(firstOperand, secondOperand);
	}

	@Override
	public synchronized Primitive widen(char type, Primitive arg) throws InvalidOperandException, InvalidTypeException {
		return super.widen(type, arg);
	}

	@Override
	public synchronized Primitive narrow(char type, Primitive arg) throws InvalidOperandException, InvalidTypeException {
		return super.narrow(type, arg);
	}

	@Override
	public synchronized Primitive applyFunctionPrimitive(char type, HistoryPoint historyPoint, String operator, Value... args)
			throws InvalidOperandException, InvalidTypeException {
		return super.applyFunctionPrimitive(type, historyPoint, operator, args);
	}
	
	@Override
	public synchronized Primitive applyUnary(Operator operator, Primitive operand)
			throws InvalidOperatorException, InvalidOperandException, InvalidTypeException {
		return super.applyUnary(operator, operand);
	}
	
	@Override
	public synchronized Primitive applyBinary(Primitive firstOperand, Operator operator, Primitive secondOperand)
			throws InvalidOperatorException, InvalidOperandException, InvalidTypeException {
		return super.applyBinary(firstOperand, operator, secondOperand);
	}
	
	@Override
	public synchronized Primitive to(char type, Primitive arg) throws InvalidOperandException, InvalidTypeException {
		return super.to(type, arg);
	}
	
	@Override
	public synchronized Primitive simplify(Primitive arg) {
		return super.simplify(arg);
	}
}
