package com.linxcool.view;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 输入流过滤器<p>
 * 兼容低版本解析网络图片产生 decoder->decode returned false 错误<br>
 * <code> BitmapFactory.decodeStream(new FlushedInputStream(is)) </code>
 * @author: 胡昌海(linxcool.hu)
 */
public class FlushedInputStream extends FilterInputStream{

	protected FlushedInputStream(InputStream in) {
		super(in);
	}

	@Override
	public long skip(long count) throws IOException {
		long skipped = 0;
		while(skipped < count){
			long needSkip = count - skipped;
			long resultSkip = in.skip(needSkip);
			if(resultSkip == 0L){
				int b = in.read();
				if(b < 0) break;
				else resultSkip = 1;
			}
			skipped += resultSkip;
		}
		return skipped;
	}
}