package rdf;

import com.hp.hpl.jena.sparql.algebra.Op;

public class ServiceObj implements Comparable<ServiceObj>{
	//custom class to store both service call algebra and cardinality.
	//used for easier sorting. 
		private Op serviceCall;
		private int cardinality;
		
		//Unknown cardinality => infinity!
		public ServiceObj(Op op)	{
			serviceCall = op;
			cardinality = Integer.MAX_VALUE;
		}
		
		public ServiceObj(Op op, int i)	{
			serviceCall = op;
			cardinality = i;
		}
		
		Op getOp()	{ return serviceCall; }
		int getCard()	{ return cardinality; }
		
		@Override
		public int compareTo(ServiceObj other)	{
			if (this.cardinality == other.cardinality) return 0;
			return (this.cardinality - other.cardinality); 	//NEED TO TEST THIS
		}
		
		@Override
		public String toString()	{
			return cardinality + " : " + serviceCall;
		
		}

		
		//hashCode + equals only compares ServiceCall op, not cardinality.
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((serviceCall == null) ? 0 : serviceCall.hashCode());
			return result;
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			ServiceObj other = (ServiceObj) obj;
			if (serviceCall == null) {
				if (other.serviceCall != null) {
					return false;
				}
			} else if (!serviceCall.equals(other.serviceCall)) {
				return false;
			}
			return true;
		}
		

}
